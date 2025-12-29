package com.watyouface.security;

import com.watyouface.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI(); // <-- beaucoup plus fiable que getServletPath()

        boolean skip =
            path.startsWith("/uploads/") ||
            path.startsWith("/static/") ||
            path.startsWith("/avatars/") ||          // <---- AJOUT ICI
            path.startsWith("/media/avatars/") ||    // <---- AJOUT ICI
            path.startsWith("/api/auth/") ||
            path.startsWith("/ws") ||
            path.contains("/websocket") ||
            path.contains("/xhr") ||
            path.contains("/info");


        if (skip) {
            System.out.println("➡️ JWT Filter SKIP path=" + path);
        }

        return skip;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String authHeader = request.getHeader("Authorization");

        System.out.println("➡️ JWT Filter path=" + path + " authHeader=" + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.extractUserId(token);
                String username = jwtUtil.extractUsername(token);

                System.out.println("➡️ JWT valid token. userId=" + userId + " username=" + username);

                if (userId != null) {
                    try {
                        var userDetails = userService.loadUserById(userId);

                        var authorities = userDetails.getAuthorities();
                        if (authorities == null || authorities.isEmpty()) {
                            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                        }

                        var authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("➡️ Authentication set for user=" + username);
                    } catch (Exception e) {
                        System.err.println("⚠️ JWT invalide : utilisateur non trouvé (id=" + userId + ")");
                    }
                }
            } else {
                System.out.println("⚠️ JWT token invalide");
            }
        } else {
            System.out.println("⚠️ Pas de JWT dans le header Authorization");
        }

        filterChain.doFilter(request, response);
    }
}
