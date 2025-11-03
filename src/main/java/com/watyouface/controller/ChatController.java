// src/main/java/com/watyouface/controller/ChatController.java
package com.watyouface.controller;

import com.watyouface.dto.MessageDTO;
import com.watyouface.entity.Message;
import com.watyouface.config.StompPrincipal;
import com.watyouface.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
public class ChatController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    // payload expected: { conversationId: 1, content: "hi" }
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO incoming, Principal principal) {
        if (!(principal instanceof StompPrincipal)) {
            return; // ou throw
        }
        Long senderId = ((StompPrincipal) principal).getUserId();
        Message saved = messageService.sendMessage(incoming.getConversationId(), senderId, incoming.getContent());
        MessageDTO dto = new MessageDTO(saved);
        messagingTemplate.convertAndSend("/topic/conversations/" + incoming.getConversationId(), dto);
    }
}
