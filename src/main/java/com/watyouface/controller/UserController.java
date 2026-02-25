package com.watyouface.controller;

import com.watyouface.entity.User;
import com.watyouface.media.AvatarService;
import com.watyouface.security.Authz;
import com.watyouface.service.UserService;
import com.watyouface.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
// ✅ Laisse la config CORS globale (SecurityConfig) faire le travail
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private Authz authz;

    @Autowired
    private WalletService walletService;

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
        authz.ownerOrAdmin(id);

        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé"));
    }
}
