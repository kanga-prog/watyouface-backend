package com.watyouface.controller;

import com.watyouface.entity.Conversation;
import com.watyouface.security.Authz;
import com.watyouface.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final Authz authz;

    public ConversationController(ConversationService conversationService, Authz authz) {
        this.conversationService = conversationService;
        this.authz = authz;
    }

    // 🔹 Liste les conversations d’un utilisateur
    @GetMapping
    public List<Conversation> getMyConversations() {
        Long userId = authz.me();
        return conversationService.FindByUser(userId);
    }

    // 🔹 Crée une conversation privée
    @PostMapping("/private/{otherUserId}")
    public Conversation startPrivate(@PathVariable Long otherUserId) {
        Long me = authz.me();
        return conversationService.GetOrCreatePrivate(me, otherUserId);
    }

    // 🔹 Crée un groupe
    @PostMapping("/group")
    public Conversation createGroup(@RequestBody Map<String, Object> body) {
        authz.me(); // juste pour exiger l'auth (créateur = user connecté)

        String title = (String) body.get("title");
        List<?> rawIds = (List<?>) body.get("participantIds");

        List<Long> participantIds = rawIds.stream()
                .map(id -> Long.valueOf(id.toString()))
                .toList();

        return conversationService.CreateGroup(title, participantIds);
    }

    // 🔹 Créer ou récupérer une conversation avec un autre utilisateur
    @PostMapping("/with/{userId}")
    public ResponseEntity<Conversation> getOrCreateConversation(@PathVariable Long userId) {
        Long currentUserId = authz.me();
        Conversation conv = conversationService.GetOrCreatePrivate(currentUserId, userId);
        return ResponseEntity.ok(conv);
    }
}
