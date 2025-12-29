// src/main/java/com/watyouface/config/JwtChannelInterceptor.java
package com.watyouface.config;

import com.watyouface.security.JwtUtil;
import com.watyouface.repository.UserRepository;
import com.watyouface.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // 🟢 Intercepte le CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty()) {
                System.err.println("❌ Aucun header Authorization envoyé via STOMP");
                return null;
            }

            String raw = authHeaders.get(0);
            if (!raw.startsWith("Bearer ")) {
                System.err.println("❌ Format Authorization incorrect");
                return null;
            }

            String token = raw.substring(7);

            if (!jwtUtil.validateToken(token)) {
                System.err.println("❌ Token JWT invalide");
                return null;
            }

            Long userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                System.err.println("❌ Aucun userId dans le token");
                return null;
            }

            Optional<User> uOpt = userRepository.findById(userId);

            if (uOpt.isEmpty()) {
                System.err.println("❌ Utilisateur inexistant en base (id=" + userId + ")");
                return null;
            }

            User user = uOpt.get();

            // 🟢 On construit ton principal custom
            StompPrincipal principal =
                    new StompPrincipal(user.getUsername(), user.getId(), user.getAvatarUrl());

            accessor.setUser(principal);

            System.out.println("🔐 WebSocket CONNECT authentifié pour: " + user.getUsername());
        }

        return message;
    }
}
