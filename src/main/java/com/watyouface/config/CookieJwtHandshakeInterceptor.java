package com.watyouface.config;

import com.watyouface.entity.User;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.AuthCookieFactory;
import com.watyouface.security.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class CookieJwtHandshakeInterceptor implements HandshakeInterceptor {
    public static final String AUTHENTICATED_PRINCIPAL = CookieJwtHandshakeInterceptor.class.getName() + ".principal";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public CookieJwtHandshakeInterceptor(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = authCookie(request.getHeaders().getFirst(HttpHeaders.COOKIE));
        if (token == null || !jwtUtil.validateToken(token)) return true;

        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) return true;

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            attributes.put(AUTHENTICATED_PRINCIPAL,
                    new StompPrincipal(user.getUsername(), user.getId(), user.getAvatarUrl()));
        }
        return true;
    }

    private String authCookie(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) return null;
        for (String part : cookieHeader.split(";")) {
            String value = part.trim();
            String prefix = AuthCookieFactory.COOKIE_NAME + "=";
            if (value.startsWith(prefix)) return value.substring(prefix.length());
        }
        return null;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // No token or credential data is logged here.
    }
}
