package com.watyouface.controller;

import com.watyouface.dto.LoginRequest;
import com.watyouface.dto.RegisterRequest;
import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.JwtUtil;
import com.watyouface.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // 🔹 Enregistrement
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.isAcceptTerms()
            );

            return ResponseEntity.ok(Map.of(
                    "userId", user.getId(),
                    "needsContractAcceptance", !request.isAcceptTerms(),
                    "message", request.isAcceptTerms()
                            ? "Inscription réussie et contrat accepté."
                            : "Compte créé. Veuillez accepter le contrat pour finaliser."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🔹 Connexion
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // ✅ Rôle réel depuis la DB (fallback USER)
        String role = (user.getRole() != null) ? user.getRole().name() : "USER";

        // ✅ Token avec rôle + username
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("avatarUrl", user.getAvatarUrl());
        response.put("role", role);

        return ResponseEntity.ok(response);
    }
}
