package com.watyouface.security;

import com.watyouface.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();

        boolean skip =
                path.startsWith("/uploads/") ||
                path.startsWith("/static/") ||
                path.startsWith("/avatars/") ||
                path.startsWith("/media/avatars/") ||
                path.startsWith("/api/auth/") ||
                path.startsWith("/api/contracts/active") ||
                path.startsWith("/ws") ||
                path.contains("/websocket") ||
                path.contains("/xhr") ||
                path.contains("/info");

        return skip;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.extractUserId(token);

                if (userId != null) {
                    try {
                        var userDetails = userService.loadUserById(userId);

                        String role = jwtUtil.extractRole(token);
                        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                        var authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    } catch (Exception e) {
                        System.err.println("⚠️ JWT invalide : utilisateur non trouvé (id=" + userId + ")");
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
