package com.watyouface;

import com.watyouface.dto.MessageDTO;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.MessageRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.service.MessageService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageValidationTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void dtoRejectsBlankAndOverlongMessagesAndAcceptsValidContent() {
        MessageDTO message = new MessageDTO();
        message.setContent("   ");
        assertFalse(validator.validate(message).isEmpty());

        message.setContent("a".repeat(2001));
        assertFalse(validator.validate(message).isEmpty());

        message.setContent("message valide");
        assertTrue(validator.validate(message).isEmpty());
    }

    @Test
    void sharedServiceGuardRejectsInvalidContentForRestAndStompCallers() {
        MessageService service = new MessageService(
                mock(MessageRepository.class), mock(ConversationRepository.class), mock(UserRepository.class));

        assertThrows(IllegalArgumentException.class, () -> service.sendMessage(1L, 2L, " "));
        assertThrows(IllegalArgumentException.class, () -> service.sendMessage(1L, 2L, "x".repeat(2001)));
        assertThrows(IllegalArgumentException.class, () -> service.sendMessage(null, 2L, "message"));
    }
}
