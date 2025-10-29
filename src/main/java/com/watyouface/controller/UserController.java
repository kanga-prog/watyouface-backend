package com.watyouface.controller;

import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔐 Récupérer le profil de l'utilisateur connecté
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token manquant ou invalide");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalide ou expiré");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Utilisateur introuvable");
            }

            User user = userOpt.get();
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "acceptedContract", user.isAcceptedContract(),
                "contractVersion", user.getAcceptedContractVersion() != null 
                    ? user.getAcceptedContractVersion().getVersion() 
                    : "N/A",
                "avatarUrl", user.getAvatarUrl() != null 
                    ? user.getAvatarUrl() 
                    : ""
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Impossible d'extraire l'utilisateur du token");
        }
    }

    // ✏️ Mise à jour du profil (username, avatar)
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> updates) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token manquant ou invalide");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalide ou expiré");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Utilisateur introuvable");
            }

            User user = userOpt.get();

            if (updates.containsKey("username")) {
                String newUsername = updates.get("username");
                if (newUsername != null && !newUsername.trim().isEmpty()) {
                    user.setUsername(newUsername.trim());
                }
            }
            if (updates.containsKey("avatarUrl")) {
                user.setAvatarUrl(updates.get("avatarUrl"));
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "avatarUrl", user.getAvatarUrl() != null 
                    ? user.getAvatarUrl() 
                    : ""
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erreur lors de la mise à jour du profil");
        }
    }
}