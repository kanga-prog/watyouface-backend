package com.watyouface.controller;

import com.watyouface.dto.RegisterRequest; 
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

    // Endpoint pour l'inscription
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        System.out.println("acceptTerms: " + request.isAcceptTerms());
        if (!request.isAcceptTerms()) {
            return ResponseEntity.badRequest().body("Vous devez accepter le contrat WatYouFace pour vous inscrire.");
        }

        String response = authService.register(request.getUsername(),
                                               request.getEmail(),
                                               request.getPassword(),
                                               request.isAcceptTerms()
        );
        return ResponseEntity.ok(response);
    }

    // Endpoint pour la connexion
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        String token = authService.login(email, password);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        // Ici tu pourrais ajouter le token à une blacklist pour l’invalider
        return ResponseEntity.ok("Déconnexion réussie");
}
}
