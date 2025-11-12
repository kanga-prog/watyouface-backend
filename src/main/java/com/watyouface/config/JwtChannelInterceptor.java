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
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> auth = accessor.getNativeHeader("Authorization");
            if (auth == null || auth.isEmpty()) {
                System.err.println("❌ Aucun token JWT dans l’en-tête STOMP");
                return null;
            }

            String header = auth.get(0);
            String token = header.replace("Bearer ", "");

            if (!jwtUtil.validateToken(token)) {
                System.err.println("❌ Token JWT invalide");
                return null;
            }

            // ✅ On extrait désormais l’ID depuis le token
            Long userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                System.err.println("❌ Aucun userId dans le token");
                return null;
            }

            Optional<User> uOpt = userRepository.findById(userId);
            if (uOpt.isEmpty()) {
                System.err.println("❌ Utilisateur non trouvé pour userId=" + userId);
                return null;
            }

            User user = uOpt.get();
            accessor.setUser(new StompPrincipal(user.getUsername(), user.getId(),user.getAvatarUrl()));
        }

        return message;
    }
}
