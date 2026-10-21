package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.exception.StepUpDeliveryException;
import com.watyouface.exception.StepUpRateLimitException;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.StepUpAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

class StepUpChallengeServiceTests {
    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final StepUpCodeSender sender = mock(StepUpCodeSender.class);
    private final MutableClock clock = new MutableClock(Instant.parse("2026-10-20T10:00:00Z"));
    private final AtomicReference<String> deliveredCode = new AtomicReference<>();
    private StepUpChallengeService service;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("step-user", "old@example.test", "hash:current-passphrase");
        user.setId(41L);
        when(users.findByIdAndDeletedAtIsNull(41L)).thenReturn(Optional.of(user));
        when(encoder.encode(anyString())).thenAnswer(call -> "hash:" + call.getArgument(0));
        when(encoder.matches(anyString(), anyString())).thenAnswer(call ->
                ("hash:" + call.getArgument(0)).equals(call.getArgument(1)));
        org.mockito.Mockito.doAnswer(call -> {
            deliveredCode.set(call.getArgument(2));
            return null;
        }).when(sender).send(anyString(), any(StepUpAction.class), anyString());
        service = new StepUpChallengeService(users, encoder, sender, true,
                300, 5, 3, 900, clock);
    }

    @Test
    void sendsCodeToCurrentEmailAndReturnsOnlyExpiry() {
        Instant expiresAt = service.requestCode(41L, StepUpAction.EMAIL_CHANGE, "current-passphrase");

        assertThat(expiresAt).isEqualTo(clock.instant().plusSeconds(300));
        assertThat(deliveredCode.get()).matches("\\d{6}");
        verify(sender).send("old@example.test", StepUpAction.EMAIL_CHANGE, deliveredCode.get());
    }

    @Test
    void wrongCurrentPasswordDoesNotSendCode() {
        assertThatThrownBy(() -> service.requestCode(41L, StepUpAction.EMAIL_CHANGE, "wrong"))
                .isInstanceOf(SecurityException.class);
        verify(sender, never()).send(anyString(), any(StepUpAction.class), anyString());
    }

    @Test
    void rejectsInvalidCodesAndLocksChallengeAfterFiveAttempts() {
        service.requestCode(41L, StepUpAction.EMAIL_CHANGE, "current-passphrase");
        for (int attempt = 0; attempt < 5; attempt++) {
            assertThatThrownBy(() -> service.verifyCode(41L, StepUpAction.EMAIL_CHANGE, "000000"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        assertThatThrownBy(() -> service.verifyCode(41L, StepUpAction.EMAIL_CHANGE, deliveredCode.get()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void expiredChallengeAndProofAreRejected() {
        service.requestCode(41L, StepUpAction.EMAIL_CHANGE, "current-passphrase");
        clock.advanceSeconds(301);
        assertThatThrownBy(() -> service.verifyCode(41L, StepUpAction.EMAIL_CHANGE, deliveredCode.get()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void codeIsSingleUseAndProofIsBoundToActionAndUser() {
        service.requestCode(41L, StepUpAction.EMAIL_CHANGE, "current-passphrase");
        var proof = service.verifyCode(41L, StepUpAction.EMAIL_CHANGE, deliveredCode.get());

        assertThatThrownBy(() -> service.verifyCode(41L, StepUpAction.EMAIL_CHANGE, deliveredCode.get()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.consume(41L, StepUpAction.PASSWORD_CHANGE, proof.token()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.consume(99L, StepUpAction.EMAIL_CHANGE, proof.token()))
                .isInstanceOf(IllegalArgumentException.class);
        service.consume(41L, StepUpAction.EMAIL_CHANGE, proof.token());
        assertThatThrownBy(() -> service.consume(41L, StepUpAction.EMAIL_CHANGE, proof.token()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requestRateLimitReturnsRetryWindow() {
        for (int request = 0; request < 3; request++) {
            service.requestCode(41L, StepUpAction.PASSWORD_CHANGE, "current-passphrase");
        }
        assertThatThrownBy(() -> service.requestCode(41L, StepUpAction.EMAIL_CHANGE, "current-passphrase"))
                .isInstanceOf(StepUpRateLimitException.class)
                .satisfies(error -> assertThat(((StepUpRateLimitException) error).getRetryAfterSeconds()).isEqualTo(900));
    }

    @Test
    void disabledMailFailsClosed() {
        StepUpChallengeService disabled = new StepUpChallengeService(users, encoder, sender,
                false, 300, 5, 3, 900, clock);
        assertThatThrownBy(() -> disabled.requestCode(41L, StepUpAction.EMAIL_CHANGE, "current-passphrase"))
                .isInstanceOf(StepUpDeliveryException.class);
    }

    private static final class MutableClock extends Clock {
        private Instant now;

        private MutableClock(Instant now) { this.now = now; }
        private void advanceSeconds(long seconds) { now = now.plusSeconds(seconds); }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }
}
