// src/main/java/com/watyouface/config/JwtChannelInterceptor.java
package com.watyouface.config;

import com.watyouface.security.JwtUtil;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Autowired private JwtUtil jwtUtil;

    @Value("${app.security.jwt.allow-bearer-fallback:false}")
    private boolean allowBearerFallback;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    private static final Pattern CONVERSATION_TOPIC = Pattern.compile("^/topic/conversations/(\\d+)$");
    private static final String CHAT_SEND_DESTINATION = "/app/chat.sendMessage";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // 🟢 Intercepte le CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            // Preferred path: the HTTP/SockJS handshake authenticated the HttpOnly cookie.
            if (accessor.getUser() instanceof StompPrincipal) {
                return message;
            }

            // Transitional local/dev-only compatibility; disabled in production.
            if (!allowBearerFallback) return null;

            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders == null || authHeaders.isEmpty()) {
                return null;
            }

            String raw = authHeaders.get(0);
            if (!raw.startsWith("Bearer ")) {
                return null;
            }

            String token = raw.substring(7);

            if (!jwtUtil.validateToken(token)) {
                return null;
            }

            Long userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                return null;
            }

            Optional<User> uOpt = userRepository.findById(userId);

            if (uOpt.isEmpty()) {
                return null;
            }

            User user = uOpt.get();

            // 🟢 On construit ton principal custom
            StompPrincipal principal =
                    new StompPrincipal(user.getUsername(), user.getId(), user.getAvatarUrl());

            accessor.setUser(principal);

        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            if (!(accessor.getUser() instanceof StompPrincipal principal)) {
                return null;
            }
            String destination = accessor.getDestination();
            Matcher matcher = destination == null ? null : CONVERSATION_TOPIC.matcher(destination);
            if (matcher == null || !matcher.matches() || !conversationRepository.existsByIdAndParticipants_User_Id(
                    Long.valueOf(matcher.group(1)), principal.getUserId())) {
                return null;
            }
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            if (!(accessor.getUser() instanceof StompPrincipal)
                    || !CHAT_SEND_DESTINATION.equals(accessor.getDestination())) {
                return null;
            }
        }

        return message;
    }
}
