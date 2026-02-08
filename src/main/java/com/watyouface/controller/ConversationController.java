package com.watyouface.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import com.watyouface.entity.Conversation;
import com.watyouface.service.ConversationService;
import com.watyouface.security.JwtUtil;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final JwtUtil jwtUtil;

    public ConversationController(ConversationService conversationService, JwtUtil jwtUtil) {
        this.conversationService = conversationService;
        this.jwtUtil = jwtUtil;
    }

    // 🔹 Liste les conversations d’un utilisateur
    @GetMapping
    public List<Conversation> getMyConversations(@RequestHeader("Authorization") String auth) {
        Long userId = jwtUtil.getUserIdFromHeader(auth);
        return conversationService.FindByUser(userId); // méthode capitalisée
    }

    // 🔹 Crée une conversation privée
    @PostMapping("/private/{otherUserId}")
    public Conversation startPrivate(@PathVariable Long otherUserId,
                                     @RequestHeader("Authorization") String auth) {
        Long me = jwtUtil.getUserIdFromHeader(auth);
        return conversationService.GetOrCreatePrivate(me, otherUserId); // méthode capitalisée
    }

    // 🔹 Crée un groupe
    @PostMapping("/group")
    public Conversation createGroup(@RequestBody Map<String, Object> body,
                                    @RequestHeader("Authorization") String auth) {
        String title = (String) body.get("title");
         List<?> rawIds = (List<?>) body.get("participantIds");

    List<Long> participantIds = rawIds.stream()
            .map(id -> Long.valueOf(id.toString()))
            .toList();

    return conversationService.CreateGroup(title, participantIds);
}

    // 🔹 Créer ou récupérer une conversation avec un autre utilisateur
    @PostMapping("/with/{userId}")
    public ResponseEntity<Conversation> getOrCreateConversation(@PathVariable Long userId,
                                                                @RequestHeader("Authorization") String authHeader) {
        Long currentUserId = jwtUtil.getUserIdFromHeader(authHeader);
        Conversation conv = conversationService.GetOrCreatePrivate(currentUserId, userId); // appel correct
        return ResponseEntity.ok(conv);
    }
}
    