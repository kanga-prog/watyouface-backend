package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.exception.ResourceNotFoundException;
import com.watyouface.exception.StepUpDeliveryException;
import com.watyouface.exception.StepUpRateLimitException;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.StepUpAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory, per-instance, one-time step-up challenges. No OTP/token is persisted. */
@Service
public class StepUpChallengeService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final StepUpCodeSender codeSender;
    private final boolean mailEnabled;
    private final long challengeTtlMillis;
    private final int maxCodeAttempts;
    private final int requestLimit;
    private final long requestWindowMillis;
    private final Clock clock;
    private final ConcurrentHashMap<ChallengeKey, Challenge> challenges = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, RequestWindow> requestWindows = new ConcurrentHashMap<>();

    @Autowired
    public StepUpChallengeService(UserRepository users, PasswordEncoder passwordEncoder,
                                  StepUpCodeSender codeSender,
                                  @Value("${spring.mail.enabled:false}") boolean mailEnabled,
                                  @Value("${app.security.step-up.challenge-ttl-seconds:300}") long ttlSeconds,
                                  @Value("${app.security.step-up.max-code-attempts:5}") int maxCodeAttempts,
                                  @Value("${app.security.step-up.request-limit:3}") int requestLimit,
                                  @Value("${app.security.step-up.request-window-seconds:900}") long requestWindowSeconds) {
        this(users, passwordEncoder, codeSender, mailEnabled, ttlSeconds, maxCodeAttempts,
                requestLimit, requestWindowSeconds, Clock.systemUTC());
    }

    StepUpChallengeService(UserRepository users, PasswordEncoder passwordEncoder,
                           StepUpCodeSender codeSender, boolean mailEnabled,
                           long ttlSeconds, int maxCodeAttempts, int requestLimit,
                           long requestWindowSeconds, Clock clock) {
        if (ttlSeconds < 1 || ttlSeconds > 600 || maxCodeAttempts < 1
                || requestLimit < 1 || requestWindowSeconds < 1) {
            throw new IllegalArgumentException("Invalid step-up policy configuration");
        }
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.codeSender = codeSender;
        this.mailEnabled = mailEnabled;
        this.challengeTtlMillis = ttlSeconds * 1000;
        this.maxCodeAttempts = maxCodeAttempts;
        this.requestLimit = requestLimit;
        this.requestWindowMillis = requestWindowSeconds * 1000;
        this.clock = clock;
    }

    public Instant requestCode(Long userId, StepUpAction action, String currentPassword) {
        if (!mailEnabled) throw new StepUpDeliveryException();
        User user = activeUser(userId);
        verifyCurrentPassword(user, currentPassword);

        long now = clock.millis();
        cleanupExpired(now);
        enforceRequestLimit(userId, now);

        String code = String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000));
        Instant expiresAt = Instant.ofEpochMilli(now + challengeTtlMillis);
        ChallengeKey key = new ChallengeKey(userId, action);
        Challenge challenge = new Challenge(passwordEncoder.encode(code), now, expiresAt);
        challenges.put(key, challenge);
        try {
            // EMAIL_CHANGE intentionally goes to the currently stored (old) address.
            codeSender.send(user.getEmail(), action, code);
        } catch (RuntimeException exception) {
            challenges.remove(key, challenge);
            throw new StepUpDeliveryException();
        }
        return expiresAt;
    }

    public VerifiedStepUp verifyCode(Long userId, StepUpAction action, String code) {
        long now = clock.millis();
        ChallengeKey key = new ChallengeKey(userId, action);
        Challenge challenge = challenges.get(key);
        if (challenge == null) throw invalidChallenge();

        synchronized (challenge) {
            if (challenge.expiresAt.toEpochMilli() <= now) {
                challenges.remove(key, challenge);
                throw invalidChallenge();
            }
            if (challenge.used) throw invalidChallenge();
            challenge.attempts++;
            if (challenge.attempts > maxCodeAttempts
                    || !passwordEncoder.matches(code, challenge.codeHash)) {
                if (challenge.attempts >= maxCodeAttempts) challenges.remove(key, challenge);
                throw invalidChallenge();
            }

            // The OTP is one-use. Only its separate short-lived proof can complete the action.
            challenge.used = true;
            challenge.codeHash = null;
            byte[] proofBytes = new byte[32];
            RANDOM.nextBytes(proofBytes);
            String proof = Base64.getUrlEncoder().withoutPadding().encodeToString(proofBytes);
            challenge.proofHash = sha256(proof);
            return new VerifiedStepUp(proof, challenge.expiresAt);
        }
    }

    public void consume(Long userId, StepUpAction action, String proof) {
        ChallengeKey key = new ChallengeKey(userId, action);
        Challenge challenge = challenges.get(key);
        if (challenge == null || proof == null || proof.isBlank()) throw invalidChallenge();

        synchronized (challenge) {
            if (!challenge.used || challenge.proofHash == null
                    || challenge.expiresAt.toEpochMilli() <= clock.millis()
                    || !MessageDigest.isEqual(challenge.proofHash, sha256(proof))) {
                throw invalidChallenge();
            }
            challenge.proofHash = null;
            challenges.remove(key, challenge);
        }
    }

    private User activeUser(Long userId) {
        return users.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte actif introuvable"));
    }

    private void verifyCurrentPassword(User user, String currentPassword) {
        if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new SecurityException("Le mot de passe actuel est incorrect.");
        }
    }

    private void enforceRequestLimit(Long userId, long now) {
        RequestWindow window = requestWindows.computeIfAbsent(userId, ignored -> new RequestWindow(now));
        synchronized (window) {
            if (now - window.startedAtMillis >= requestWindowMillis) {
                window.startedAtMillis = now;
                window.requests = 0;
            }
            if (window.requests >= requestLimit) {
                long retryMillis = window.startedAtMillis + requestWindowMillis - now;
                throw new StepUpRateLimitException(Math.max(1, (retryMillis + 999) / 1000));
            }
            window.requests++;
        }
    }

    private void cleanupExpired(long now) {
        challenges.entrySet().removeIf(entry -> entry.getValue().expiresAt.toEpochMilli() <= now);
        requestWindows.entrySet().removeIf(entry -> now - entry.getValue().startedAtMillis >= requestWindowMillis);
    }

    private IllegalArgumentException invalidChallenge() {
        return new IllegalArgumentException("Code ou autorisation de vérification invalide, expiré ou déjà utilisé.");
    }

    private byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 indisponible", impossible);
        }
    }

    public record VerifiedStepUp(String token, Instant expiresAt) { }
    private record ChallengeKey(Long userId, StepUpAction action) { }

    private static final class Challenge {
        private String codeHash;
        private final long createdAtMillis;
        private final Instant expiresAt;
        private int attempts;
        private boolean used;
        private byte[] proofHash;

        private Challenge(String codeHash, long createdAtMillis, Instant expiresAt) {
            this.codeHash = codeHash;
            this.createdAtMillis = createdAtMillis;
            this.expiresAt = expiresAt;
        }
    }

    private static final class RequestWindow {
        private long startedAtMillis;
        private int requests;

        private RequestWindow(long startedAtMillis) {
            this.startedAtMillis = startedAtMillis;
        }
    }
}
