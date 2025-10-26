package com.watyouface.controller;

import com.watyouface.dto.RegisterRequest;
import com.watyouface.entity.User;
import com.watyouface.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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
                "message", request.isAcceptTerms() ?
                    "Inscription réussie et contrat accepté." :
                    "Compte créé. Veuillez accepter le contrat pour finaliser."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String result = authService.login(email, password);
        if (result.startsWith("Email") || result.startsWith("Veuillez")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok("Déconnexion réussie");
    }
}