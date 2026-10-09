package com.watyouface.controller;

import com.watyouface.dto.MessageDTO;
import com.watyouface.entity.Conversation;
import com.watyouface.entity.Message;
import com.watyouface.entity.User;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.Authz;
import com.watyouface.service.MessageService;
import com.watyouface.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final Authz authz;

    public MessageController(MessageService messageService,
                             ConversationRepository conversationRepository,
                             SimpMessagingTemplate messagingTemplate,
                             UserRepository userRepository,
                             Authz authz) {
        this.messageService = messageService;
        this.conversationRepository = conversationRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.authz = authz;
    }

    // 🔹 Liste des conversations de l’utilisateur (pagination)
    @GetMapping("/conversations")
    public Page<Conversation> myConversations(Pageable pageable) {
        Long userId = authz.me();
        return conversationRepository.findByUser(userId, pageable);
    }

    // 🔹 Messages d’une conversation (pagination)
    @GetMapping("/conversations/{id}")
    public Page<Message> getMessages(@PathVariable Long id,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "50") int size) {
        Long userId = authz.me();
        if (!conversationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conversation introuvable");
        }
        if (!conversationRepository.existsByIdAndParticipants_User_Id(id, userId)) {
            throw new SecurityException("Interdit");
        }
        return messageService.fetchMessages(id, page, size);
    }

    // 🔹 Poster un message dans une conversation avec avatar
    @PostMapping("/conversations/{id}")
    public ResponseEntity<MessageDTO> postMessage(
            @PathVariable Long id,
            @Valid @RequestBody MessageDTO body
    ) {
        Long userId = authz.me();
        if (!conversationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conversation introuvable");
        }

        Optional<User> uOpt = userRepository.findById(userId);
        if (uOpt.isEmpty()) return ResponseEntity.notFound().build();
        User sender = uOpt.get();

        // ⚠️ sécurité: vérifier que l'utilisateur est participant avant d'envoyer
        if (!conversationRepository.existsByIdAndParticipants_User_Id(id, userId)) {
            return ResponseEntity.status(403).build();
        }

        // ⚡ sendMessage retourne maintenant une LISTE (pour groupes)
        List<Message> messages = messageService.sendMessage(id, userId, body.getContent());

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
    public ResponseEntity<List<MessageDTO>> getMessagesByConversation(@PathVariable Long id) {
        Long userId = authz.me();
        if (!conversationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conversation introuvable");
        }
        if (!conversationRepository.existsByIdAndParticipants_User_Id(id, userId)) {
            return ResponseEntity.status(403).build();
        }

        List<MessageDTO> list = messageService.findByConversation(id);
        return ResponseEntity.ok(list);
    }

    // 🔹 Endpoint debug / récupération complète
    @GetMapping("/{conversationId}/all")
    public ResponseEntity<List<MessageDTO>> getMessagesRest(@PathVariable Long conversationId) {
        Long userId = authz.me();
        if (!conversationRepository.existsById(conversationId)) {
            return ResponseEntity.notFound().build();
        }
        if (!conversationRepository.existsByIdAndParticipants_User_Id(conversationId, userId)) {
            return ResponseEntity.status(403).build();
        }
        List<MessageDTO> list = messageService.findByConversation(conversationId);
        return ResponseEntity.ok(list);
    }
}
