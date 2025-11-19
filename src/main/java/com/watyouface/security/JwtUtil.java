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

    /**
     * ✅ Génération du token basé sur userId comme subject
     * et incluant username dans les claims pour compatibilité.
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username); // pour compatibilité
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId)) // 🔥 le token est basé sur l'id unique
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 🔹 Extraction de l'userId depuis le subject
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = getAllClaims(token);
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 🔹 Extraction du username depuis les claims
     */
    public String extractUsername(String token) {
        Claims claims = getAllClaims(token);
        Object username = claims.get("username");
        return username != null ? username.toString() : null;
    }

    /**
     * 🔹 Extraction du userId depuis le header complet "Bearer ..."
     */
    public Long getUserIdFromHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return extractUserId(token);
    }

    /**
     * 🔹 Validation de la signature et de la date d’expiration du token
     */
    public boolean validateToken(String token) {
        try {
            getAllClaims(token); // si ça échoue → exception
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("❌ JWT invalide : " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Récupère le corps (Claims) du token
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody(); // ✅ ici on retourne bien le corps du JWT
    }

    public boolean validateTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        String token = authHeader.substring(7);
        return validateToken(token);
    }

}
