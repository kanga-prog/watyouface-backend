package com.watyouface.controller;

import com.watyouface.entity.User;
import com.watyouface.service.UserService;
import com.watyouface.security.JwtUtil;
import com.watyouface.media.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AvatarService avatarService;

    // 🔐 GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.getUserIdFromHeader(authHeader);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "acceptedContract", user.isAcceptedContract(),
                "contractVersion", user.getAcceptedContractVersion() != null
                        ? user.getAcceptedContractVersion().getVersion()
                        : "N/A",
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "/uploads/avatars/default.png"
        ));
    }

    // ✏️ PUT /api/users/update
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody Map<String, String> updates) {

        Long userId = jwtUtil.getUserIdFromHeader(authHeader);
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (updates.containsKey("username")) {
            String newUsername = updates.get("username");
            if (newUsername != null && !newUsername.trim().isEmpty()) {
                user.setUsername(newUsername.trim());
            }
        }

        userService.saveUser(user);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "/uploads/avatars/default.png"
        ));
    }

    // ✅ POST /api/users/avatar
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          @RequestHeader("Authorization") String authHeader) {

        try {
            Long userId = jwtUtil.getUserIdFromHeader(authHeader);

            // ⚡ Utilisation de AvatarService pour sauvegarder
            String avatarUrl = avatarService.saveAvatar(file, userId);

            // Mettre à jour le user
            User updatedUser = userService.updateAvatarUrl(userId, avatarUrl);

            return ResponseEntity.ok(Map.of(
                    "message", "Avatar mis à jour avec succès",
                    "avatarUrl", updatedUser.getAvatarUrl()
            ));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de l'upload de l'avatar"));
        }
    }

    // 🔹 GET /api/users (infos publiques)
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {

        if (!jwtUtil.validateTokenFromHeader(authHeader)) {
            return ResponseEntity.status(401).body("Token manquant ou invalide");
        }

        var users = userService.getAllUsers().stream()
                .map(u -> Map.of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "/uploads/avatars/default.png"
                ))
                .toList();

        return ResponseEntity.ok(users);
    }
}
