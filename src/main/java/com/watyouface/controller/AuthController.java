package com.watyouface.controller;

import com.watyouface.dto.LoginRequest;
import com.watyouface.dto.RegisterRequest;
import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.JwtUtil;
import com.watyouface.security.LoginRateLimiter;
import com.watyouface.security.AuthCookieFactory;
import com.watyouface.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRateLimiter loginRateLimiter;

    @Autowired
    private AuthCookieFactory authCookieFactory;

    @GetMapping("/csrf")
    public Map<String, String> csrfToken(CsrfToken csrfToken) {
        return Map.of("token", csrfToken.getToken());
    }

    // 🔹 Enregistrement
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        String clientKey = httpRequest.getRemoteAddr();
        long retryAfter = loginRateLimiter.retryAfterSeconds(clientKey);
        if (retryAfter > 0) {
            return ResponseEntity.status(429)
                    .header(HttpHeaders.RETRY_AFTER, Long.toString(retryAfter))
                    .body(Map.of("error", "Trop de tentatives. Réessayez plus tard."));
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            loginRateLimiter.recordFailure(clientKey);
            return ResponseEntity.status(401).body(Map.of("error", "Identifiants invalides"));
        }
        loginRateLimiter.clear(clientKey);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        try {
            authService.ensureActiveContractAccepted(user);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Acceptation du contrat requise"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("error", "Contrat actif indisponible"));
        }

        // ✅ Rôle réel depuis la DB (fallback USER)
        String role = (user.getRole() != null) ? user.getRole().name() : "USER";

        // ✅ Token avec rôle + username (pas email)
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), role);

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("avatarUrl", user.getAvatarUrl());
        response.put("role", role);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieFactory.create(token).toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieFactory.clear().toString())
                .build();
    }
}
