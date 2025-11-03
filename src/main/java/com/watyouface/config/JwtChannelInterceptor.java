// src/main/java/com/watyouface/config/JwtChannelInterceptor.java
package com.watyouface.config;

import com.watyouface.security.JwtUtil;
import com.watyouface.repository.UserRepository;
import com.watyouface.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
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
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompHeaderAccessor.Command.CONNECT.equals(accessor.getCommand())) {
            List<String> auth = accessor.getNativeHeader("Authorization");
            if (auth == null || auth.isEmpty()) {
                // pas d'auth -> laisser passer si tu acceptes anonymes, sinon bloquer
                return message; // ou throw new IllegalArgumentException("Missing token");
            }
            String header = auth.get(0);
            String token = header.replace("Bearer ", "");
            if (!jwtUtil.validateToken(token)) {
                // token invalide -> bloquer
                return null;
            }
            // Exemple: on suppose que JwtUtil a method extractUsername et/ou extractUserId
            String username = jwtUtil.extractUsername(token);
            Optional<User> uOpt = userRepository.findByUsername(username);
            if (uOpt.isEmpty()) return null;
            User user = uOpt.get();
            accessor.setUser(new StompPrincipal(user.getUsername(), user.getId()));
        }
        return message;
    }
}
