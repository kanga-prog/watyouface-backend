package com.watyouface.controller;

import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/avatars/";

    // 🔐 GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        System.out.println("➡️ GET /api/users/me called");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token manquant ou invalide");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalide ou expiré");
        }

        String email = jwtUtil.extractUsername(token);
        System.out.println("🔍 Email extrait du token: " + email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            System.out.println("❌ Utilisateur introuvable pour l’email: " + email);
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
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "/uploads/avatars/default.png"
        ));
    }

    // ✏️ PUT /api/users/update
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody Map<String, String> updates) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token manquant ou invalide");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalide ou expiré");
        }

        String email = jwtUtil.extractUsername(token);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("Utilisateur introuvable");

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
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "/uploads/avatars/default.png"
        ));
    }

    // ✅ POST /api/users/avatar
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file,
                                          @RequestHeader("Authorization") String authHeader) throws IOException {

        Long userId = jwtUtil.getUserIdFromHeader(authHeader);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Aucun fichier envoyé"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE)
                || contentType.equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Type de fichier non autorisé (png/jpg/jpeg uniquement)"));
        }

        long maxBytes = 10L * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le fichier dépasse la taille maximale (10MB)"));
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String filename = "user_" + userId + "_" + UUID.randomUUID() + "." + ext;
        Path filePath = uploadPath.resolve(filename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String publicUrl = "/uploads/avatars/" + filename;
        user.setAvatarUrl(publicUrl);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Avatar mis à jour avec succès",
                "avatarUrl", publicUrl
        ));
    }
    // 🔹 GET /api/users
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token manquant ou invalide");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).body("Token invalide ou expiré");
        }

        // ✅ On renvoie uniquement les infos publiques (id, username, avatarUrl)
        var users = userRepository.findAll().stream()
                .map(u -> Map.of(
                        "id", u.getId(),
                        "username", u.getUsername(),
                        "avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "/uploads/avatars/default.png"
                ))
                .toList();

        return ResponseEntity.ok(users);
    }

}
