package com.watyouface.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MySuperSecretKeyForJWTsThatIsVeryLong12345!";
    private static final long EXPIRATION_TIME = 86400000; // 1 jour en ms

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ✅ Version avec rôle (à utiliser)
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Compat (si encore utilisé ailleurs)
    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, "USER");
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = getAllClaims(token);
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public String extractUsername(String token) {
        Claims claims = getAllClaims(token);
        Object username = claims.get("username");
        return username != null ? username.toString() : null;
    }

    public String extractRole(String token) {
        Claims claims = getAllClaims(token);
        Object role = claims.get("role");
        return role != null ? role.toString() : "USER";
    }

    public Long getUserIdFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return extractUserId(token);
    }

    public String getRoleFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) return "USER";
        String token = header.substring(7);
        return extractRole(token);
    }

    public boolean validateToken(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("❌ JWT invalide : " + e.getMessage());
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        String token = authHeader.substring(7);
        return validateToken(token);
    }
}
