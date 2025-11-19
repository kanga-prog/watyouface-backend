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
import org.springframework.http.ResponseEntity;
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

    // 🔹 Liste des conversations de l’utilisateur (pagination)
    @GetMapping("/conversations")
    public Page<Conversation> myConversations(@RequestHeader("Authorization") String auth, Pageable pageable) {
        Long userId = jwtUtil.getUserIdFromHeader(auth);
        return conversationRepository.findByUser(userId, pageable);
    }

    // 🔹 Messages d’une conversation (pagination)
    @GetMapping("/conversations/{id}")
    public Page<Message> getMessages(@PathVariable Long id,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "50") int size,
                                     @RequestHeader("Authorization") String auth) {
        Long userId = jwtUtil.getUserIdFromHeader(auth);
        if (!conversationRepository.existsByIdAndParticipants_User_Id(id, userId)) {
            throw new RuntimeException("Utilisateur non autorisé");
        }
        return messageService.fetchMessages(id, page, size);
    }

    // 🔹 Poster un message dans une conversation avec avatar
    @PostMapping("/conversations/{id}")
    public ResponseEntity<MessageDTO> postMessage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String auth
    ) {
        Long userId = jwtUtil.getUserIdFromHeader(auth);
        Optional<User> uOpt = userRepository.findById(userId);
        if (uOpt.isEmpty()) return ResponseEntity.notFound().build();

        User sender = uOpt.get();

        // ⚡ sendMessage retourne maintenant une LISTE (pour groupes)
        List<Message> messages = messageService.sendMessage(id, userId, body.get("content"));

        // 🔹 On renvoie au REST SEULEMENT le message du sender (le sien)
        Message senderMessage = messages.get(0);

        MessageDTO dto = new MessageDTO(senderMessage);
        dto.setSenderAvatarUrl(sender.getAvatarUrl());

        // 🔹 WebSocket broadcast
        messagingTemplate.convertAndSend("/topic/conversations/" + id, dto);

        return ResponseEntity.ok(dto);
    }


    // 🔹 Messages d’une conversation (DTO pour React)
    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getMessagesByConversation(@PathVariable Long id,
                                                                      @RequestHeader("Authorization") String auth) {
        Long userId = jwtUtil.getUserIdFromHeader(auth);
        if (!conversationRepository.existsByIdAndParticipants_User_Id(id, userId)) {
            return ResponseEntity.status(403).build();
        }

        List<MessageDTO> list = messageService.findByConversation(id);
        return ResponseEntity.ok(list);
    }

    // 🔹 Endpoint debug / récupération complète
    @GetMapping("/{conversationId}/all")
    public ResponseEntity<List<MessageDTO>> getMessagesRest(@PathVariable Long conversationId) {
        List<MessageDTO> list = messageService.findByConversation(conversationId);
        System.out.println("DEBUG messages: " + list);
        return ResponseEntity.ok(list);
    }
}
