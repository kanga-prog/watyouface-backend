package com.watyouface.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
public class AuthCookieFactory {
    public static final String COOKIE_NAME = "WATYOUFACE_AUTH";

    private final boolean secure;
    private final String sameSite;
    private final JwtUtil jwtUtil;

    public AuthCookieFactory(
            @Value("${app.security.auth-cookie.secure:false}") boolean secure,
            @Value("${app.security.auth-cookie.same-site:Lax}") String sameSite,
            JwtUtil jwtUtil) {
        if (!Set.of("Lax", "Strict", "None").contains(sameSite)) {
            throw new IllegalArgumentException("Auth cookie SameSite must be Lax, Strict, or None");
        }
        if ("None".equals(sameSite) && !secure) {
            throw new IllegalArgumentException("SameSite=None requires Secure auth cookies");
        }
        this.secure = secure;
        this.sameSite = sameSite;
        this.jwtUtil = jwtUtil;
    }

    public ResponseCookie create(String token) {
        return base().value(token).maxAge(jwtUtil.getExpirationDuration()).build();
    }

    public ResponseCookie clear() {
        return base().value("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder base() {
        return ResponseCookie.from(COOKIE_NAME)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .sameSite(sameSite);
    }
}
