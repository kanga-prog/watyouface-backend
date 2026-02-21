package com.watyouface.controller;

import com.watyouface.repository.*;
import com.watyouface.security.Authz;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.watyouface.entity.enums.Role;
import com.watyouface.entity.User;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final Authz authz;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ListingRepository listingRepository;

    public AdminController(
            Authz authz,
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            ListingRepository listingRepository
    ) {
        this.authz = authz;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.listingRepository = listingRepository;
    }

    private void requireAdmin() {
        if (!authz.isAdmin()) throw new SecurityException("Admin only");
    }

    @GetMapping("/users")
    public Object users() {
        requireAdmin();
        return userRepository.findAll();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> setRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        requireAdmin();
        String roleStr = body.getOrDefault("role", "USER");
        Role role;
        try {
            role = Role.valueOf(roleStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Role invalide"));
        }

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Role mis à jour", "role", user.getRole().name()));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        requireAdmin();
        postRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Post supprimé"));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        requireAdmin();
        commentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Commentaire supprimé"));
    }

    @DeleteMapping("/listings/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        requireAdmin();
        listingRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Annonce supprimée"));
    }
}
