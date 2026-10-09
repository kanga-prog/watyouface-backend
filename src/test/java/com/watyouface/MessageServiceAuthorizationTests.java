package com.watyouface;

import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.MessageRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.service.MessageService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessageServiceAuthorizationTests {

    @Test
    void nonParticipantCannotSendMessageToConversation() {
        ConversationRepository conversations = mock(ConversationRepository.class);
        when(conversations.existsById(42L)).thenReturn(true);
        when(conversations.existsByIdAndParticipants_User_Id(42L, 7L)).thenReturn(false);
        MessageService service = new MessageService(mock(MessageRepository.class), conversations, mock(UserRepository.class));

        assertThrows(SecurityException.class, () -> service.sendMessage(42L, 7L, "message interdit"));
    }
}
