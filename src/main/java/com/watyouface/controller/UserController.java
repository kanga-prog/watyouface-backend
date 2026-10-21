package com.watyouface.controller;

import com.watyouface.entity.User;
import com.watyouface.media.AvatarService;
import com.watyouface.security.Authz;
import com.watyouface.security.AuthCookieFactory;
import com.watyouface.service.UserService;
import com.watyouface.service.WalletService;
import com.watyouface.service.UserDataExportService;
import com.watyouface.service.AccountProfileService;
import com.watyouface.service.StepUpChallengeService;
import com.watyouface.dto.ChangeEmailRequest;
import com.watyouface.dto.ChangePasswordRequest;
import com.watyouface.dto.StepUpRequest;
import com.watyouface.dto.StepUpVerifyRequest;
import com.watyouface.dto.StepUpTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
// ✅ Laisse la config CORS globale (SecurityConfig) faire le travail
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private Authz authz;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AuthCookieFactory authCookieFactory;

    @Autowired
    private UserDataExportService userDataExportService;

    @Autowired
    private AccountProfileService accountProfileService;

    @Autowired
    private StepUpChallengeService stepUpChallengeService;

    @GetMapping(value = "/me/export", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportCurrentUserData() {
        Long userId = authz.me();
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"watyouface-data-export.json\"")
                .body(userDataExportService.exportFor(userId));
    }

    @PatchMapping("/me/email")
    public ResponseEntity<?> changeCurrentUserEmail(@Valid @RequestBody ChangeEmailRequest request) {
        Long userId = authz.me();
        User updated = accountProfileService.changeEmail(
                userId, request.getNewEmail(), request.getCurrentPassword(), request.getStepUpToken());
        return ResponseEntity.ok(Map.of("id", updated.getId(), "email", updated.getEmail()));
    }

    @PostMapping("/me/step-up/request")
    public ResponseEntity<?> requestSensitiveActionCode(@Valid @RequestBody StepUpRequest request) {
        Long userId = authz.me();
        java.time.Instant expiresAt = stepUpChallengeService.requestCode(
                userId, request.getAction(), request.getCurrentPassword());
        return ResponseEntity.accepted().body(Map.of(
                "message", "Un code temporaire a été envoyé à l’adresse e-mail enregistrée.",
                "expiresAt", expiresAt));
    }

    @PostMapping("/me/step-up/verify")
    public ResponseEntity<StepUpTokenResponse> verifySensitiveActionCode(
            @Valid @RequestBody StepUpVerifyRequest request) {
        Long userId = authz.me();
        StepUpChallengeService.VerifiedStepUp verified = stepUpChallengeService.verifyCode(
                userId, request.getAction(), request.getCode());
        return ResponseEntity.ok(new StepUpTokenResponse(verified.token(), verified.expiresAt()));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changeCurrentUserPassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = authz.me();
        accountProfileService.changePassword(userId, request.getCurrentPassword(),
                request.getNewPassword(), request.getConfirmPassword(), request.getStepUpToken());
        // The current browser session is cleared. Other copies of the stateless JWT
        // cannot be revoked centrally and remain usable until their 24-hour expiry.
        return ResponseEntity.noContent()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, authCookieFactory.clear().toString())
                .build();
    }

    // 🔐 GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Long userId = authz.me();

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Double walletBalance = walletService.getOrCreateWallet(user.getId()).getBalance();

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole() != null ? user.getRole().name() : "USER",
                "acceptedContract", user.isAcceptedContract(),
                "contractVersion", user.getAcceptedContractVersion() != null
                        ? user.getAcceptedContractVersion().getVersion()
                        : "N/A",
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "/media/avatars/default.png",
                "walletBalance", walletBalance
        ));
    }

    // ✏️ PUT /api/users/update
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> updates) {
        Long userId = authz.me();

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (updates.containsKey("username")) {
            String newUsername = updates.get("username");
            if (newUsername != null && !newUsername.trim().isEmpty()) {
                user.setUsername(newUsername.trim());
            }
        }

        userService.saveUser(user);

        Double walletBalance = walletService.getOrCreateWallet(user.getId()).getBalance();

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "/media/avatars/default.png",
                "walletBalance", walletBalance
        ));
    }

    // ✅ POST /api/users/avatar
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = authz.me();

            String avatarUrl = avatarService.saveAvatar(file, userId);
            User updatedUser = userService.updateAvatarUrl(userId, avatarUrl);

            return ResponseEntity.ok(Map.of(
                    "message", "Avatar mis à jour avec succès",
                    "avatarUrl", updatedUser.getAvatarUrl()
            ));

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Non authentifié"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de l'upload de l'avatar"));
        }
    }

    // 🔹 GET /api/users (infos publiques)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        authz.me(); // juste pour exiger l'auth (comme avant)

        var users = userService.getAllUsers().stream()
                .map(u -> Map.of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "/media/avatars/default.png"
                ))
                .toList();

        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        // ✅ owner OR admin
        Long actorId = authz.me();
        authz.ownerOrAdmin(id);
        userService.deleteUserSecured(id, actorId, authz.isAdmin());
        ResponseEntity.BodyBuilder response = ResponseEntity.ok();
        if (id.equals(actorId)) {
            response.header(org.springframework.http.HttpHeaders.SET_COOKIE, authCookieFactory.clear().toString());
        }
        return response.body(Map.of("message", "Compte effacé"));
    }
}
