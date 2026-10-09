package com.watyouface.security;

import com.watyouface.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import jakarta.servlet.http.Cookie;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final boolean allowBearerFallback;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService,
                                   @Value("${app.security.jwt.allow-bearer-fallback:false}") boolean allowBearerFallback) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.allowBearerFallback = allowBearerFallback;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/media/")
                || path.startsWith("/static/")
                || path.startsWith("/api/auth/")
                || path.startsWith("/api/contracts/active")
                || path.startsWith("/ws")
                || path.contains("/websocket")
                || path.contains("/xhr")
                || path.contains("/info");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AuthCookieFactory.COOKIE_NAME.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Transitional compatibility only; production profile disables Bearer fallback.
        if (token == null && allowBearerFallback) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) token = authHeader.substring(7);
        }

        if (token != null) {

            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.extractUserId(token);
                if (userId != null) {
                    try {
                        var userDetails = userService.loadUserById(userId);

                        var authentication = new UsernamePasswordAuthenticationToken(
                                userId, null, userDetails.getAuthorities()
                        );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (Exception ignored) {
                        // utilisateur supprimé / incohérent => pas d'auth
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
