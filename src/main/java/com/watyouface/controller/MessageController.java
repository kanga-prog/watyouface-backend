// src/main/java/com/watyouface/controller/MessageController.java
package com.watyouface.controller;

import com.watyouface.dto.MessageDTO;
import com.watyouface.entity.Conversation;
import com.watyouface.entity.Message;
import com.watyouface.entity.User;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.JwtUtil;
import com.watyouface.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService,
                             ConversationRepository conversationRepository,
                             SimpMessagingTemplate messagingTemplate,
                             JwtUtil jwtUtil,
                             UserRepository userRepository) {
        this.messageService = messageService;
        this.conversationRepository = conversationRepository;
        this.messagingTemplate = messagingTemplate;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // 🔹 Liste des conversations de l’utilisateur
    @GetMapping("/conversations")
    public Page<Conversation> myConversations(@RequestHeader("Authorization") String auth, Pageable pageable) {
        Long userId = jwtUtil.getUserIdFromHeader(auth);
        return conversationRepository.findByUser(userId, pageable);
    }

    // 🔹 Messages d’une conversation
    @GetMapping("/conversations/{id}")
    public Page<Message> getMessages(@PathVariable Long id,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "50") int size,
                                     @RequestHeader("Authorization") String auth) {
        Long userId = jwtUtil.getUserIdFromHeader(auth);
        return messageService.fetchMessages(id, page, size);
    }

    // 🔹 Poster un message dans une conversation avec avatar
    @PostMapping("/conversations/{id}")
    public MessageDTO postMessage(@PathVariable Long id,
                                  @RequestBody Map<String, String> body,
                                  @RequestHeader("Authorization") String auth) {
        Long userId = jwtUtil.getUserIdFromHeader(auth);
        Optional<User> uOpt = userRepository.findById(userId);
        if (uOpt.isEmpty()) throw new RuntimeException("Utilisateur non trouvé");

        User sender = uOpt.get();
        Message m = messageService.sendMessage(id, userId, body.get("content"));

        // Conversion en DTO avec avatar
        MessageDTO dto = new MessageDTO(m);
        dto.setSenderAvatarUrl(sender.getAvatarUrl());

        // Publication via STOMP
        messagingTemplate.convertAndSend("/topic/conversations/" + id, dto);

        return dto;
    }

    // 🔹 Route REST supplémentaire pour récupérer messages sous forme DTO
    @GetMapping("/{conversationId}/all")
    public List<MessageDTO> getMessagesRest(@PathVariable Long conversationId) {
        return messageService.findByConversation(conversationId);
    }
}
