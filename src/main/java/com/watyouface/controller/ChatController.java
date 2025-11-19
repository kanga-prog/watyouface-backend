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
import java.util.List;

@Controller
public class ChatController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDTO incoming, Principal principal) {

        if (!(principal instanceof StompPrincipal)) {
            return;
        }

        Long senderId = ((StompPrincipal) principal).getUserId();

        List<Message> savedMessages =
                messageService.sendMessage(incoming.getConversationId(), senderId, incoming.getContent());

        // 🔥 Pour chaque message (privé = 1, groupe = n)
        for (Message msg : savedMessages) {
            MessageDTO dto = new MessageDTO(msg);
            messagingTemplate.convertAndSend(
                "/topic/conversations/" + incoming.getConversationId(),
                dto
            );
        }
    }
}
