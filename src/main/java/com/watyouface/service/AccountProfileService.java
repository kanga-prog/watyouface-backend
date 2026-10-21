package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.exception.ResourceNotFoundException;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.StepUpAction;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AccountProfileService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final StepUpChallengeService stepUpChallenges;

    public AccountProfileService(UserRepository users, PasswordEncoder passwordEncoder,
                                 StepUpChallengeService stepUpChallenges) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.stepUpChallenges = stepUpChallenges;
    }

    @Transactional
    public User changeEmail(Long authenticatedUserId, String requestedEmail, String currentPassword,
                            String stepUpToken) {
        User user = activeUser(authenticatedUserId);
        verifyCurrentPassword(user, currentPassword);
        String normalizedEmail = requestedEmail.trim().toLowerCase(Locale.ROOT);

        boolean usedByAnotherAccount = users.findByEmailIgnoreCase(normalizedEmail)
                .filter(existing -> !existing.getId().equals(authenticatedUserId))
                .isPresent();
        if (usedByAnotherAccount) {
            throw new IllegalStateException("Cette adresse e-mail est déjà utilisée.");
        }

        stepUpChallenges.consume(authenticatedUserId, StepUpAction.EMAIL_CHANGE, stepUpToken);
        user.setEmail(normalizedEmail);
        try {
            return users.saveAndFlush(user);
        } catch (DataIntegrityViolationException duplicate) {
            throw new IllegalStateException("Cette adresse e-mail est déjà utilisée.");
        }
    }

    @Transactional
    public void changePassword(Long authenticatedUserId, String currentPassword,
                              String newPassword, String confirmPassword, String stepUpToken) {
        User user = activeUser(authenticatedUserId);
        verifyCurrentPassword(user, currentPassword);
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas.");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l’ancien.");
        }

        stepUpChallenges.consume(authenticatedUserId, StepUpAction.PASSWORD_CHANGE, stepUpToken);
        user.setPassword(passwordEncoder.encode(newPassword));
        users.save(user);
    }

    private User activeUser(Long userId) {
        return users.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte actif introuvable"));
    }

    private void verifyCurrentPassword(User user, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new SecurityException("Le mot de passe actuel est incorrect.");
        }
    }
}
