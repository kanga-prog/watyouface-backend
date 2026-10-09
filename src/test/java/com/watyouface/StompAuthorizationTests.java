package com.watyouface;

import com.watyouface.config.JwtChannelInterceptor;
import com.watyouface.config.StompPrincipal;
import com.watyouface.controller.ChatController;
import com.watyouface.dto.MessageDTO;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.MessageRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.JwtUtil;
import com.watyouface.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StompAuthorizationTests {

    @Test
    void subscriptionInterceptorAllowsMembersAndRejectsNonMembers() {
        ConversationRepository conversations = mock(ConversationRepository.class);
        when(conversations.existsByIdAndParticipants_User_Id(42L, 7L)).thenReturn(true);
        when(conversations.existsByIdAndParticipants_User_Id(42L, 8L)).thenReturn(false);
        JwtChannelInterceptor interceptor = new JwtChannelInterceptor();
        ReflectionTestUtils.setField(interceptor, "conversationRepository", conversations);

        Message<?> memberSubscribe = subscribe(7L);
        Message<?> outsiderSubscribe = subscribe(8L);
        Message<?> arbitrarySubscribe = message(StompCommand.SUBSCRIBE, 7L, "/topic/admin-events");
        MessageChannel channel = mock(MessageChannel.class);

        assertSame(memberSubscribe, interceptor.preSend(memberSubscribe, channel));
        assertNull(interceptor.preSend(outsiderSubscribe, channel));
        assertNull(interceptor.preSend(arbitrarySubscribe, channel));
    }

    @Test
    void stompSendRequiresAuthenticatedPrincipalAndKnownApplicationDestination() {
        JwtChannelInterceptor interceptor = new JwtChannelInterceptor();
        MessageChannel channel = mock(MessageChannel.class);

        Message<?> authenticatedChatSend = message(StompCommand.SEND, 7L, "/app/chat.sendMessage");
        Message<?> anonymousChatSend = message(StompCommand.SEND, null, "/app/chat.sendMessage");
        Message<?> arbitrarySend = message(StompCommand.SEND, 7L, "/app/admin.promote");

        assertSame(authenticatedChatSend, interceptor.preSend(authenticatedChatSend, channel));
        assertNull(interceptor.preSend(anonymousChatSend, channel));
        assertNull(interceptor.preSend(arbitrarySend, channel));
    }

    @Test
    void stompHandlerUsesAuthenticatedPrincipalRatherThanPayloadSenderId() {
        MessageService service = mock(MessageService.class);
        when(service.sendMessage(42L, 7L, "hello")).thenReturn(List.of());
        ChatController controller = new ChatController(service, mock(SimpMessagingTemplate.class));
        MessageDTO payload = new MessageDTO();
        payload.setConversationId(42L);
        payload.setSenderId(999L);
        payload.setContent("hello");

        controller.sendMessage(payload, new StompPrincipal("member", 7L, null));

        verify(service).sendMessage(42L, 7L, "hello");
        verify(service, never()).sendMessage(42L, 999L, "hello");
    }

    @Test
    void stompHandlerRejectsBlankAndOverlongMessageThroughSharedServiceGuard() {
        MessageService service = new MessageService(
                mock(MessageRepository.class), mock(ConversationRepository.class), mock(UserRepository.class));
        ChatController controller = new ChatController(service, mock(SimpMessagingTemplate.class));
        StompPrincipal principal = new StompPrincipal("member", 7L, null);

        MessageDTO blank = new MessageDTO();
        blank.setConversationId(42L);
        blank.setContent(" ");
        assertThrows(IllegalArgumentException.class, () -> controller.sendMessage(blank, principal));

        MessageDTO tooLong = new MessageDTO();
        tooLong.setConversationId(42L);
        tooLong.setContent("x".repeat(2001));
        assertThrows(IllegalArgumentException.class, () -> controller.sendMessage(tooLong, principal));
    }

    private Message<?> subscribe(Long userId) {
        return message(StompCommand.SUBSCRIBE, userId, "/topic/conversations/42");
    }

    private Message<?> message(StompCommand command, Long userId, String destination) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setDestination(destination);
        if (userId != null) accessor.setUser(new StompPrincipal("member-" + userId, userId, null));
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
