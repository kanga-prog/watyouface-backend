package com.watyouface.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import com.watyouface.entity.Conversation;
import com.watyouface.entity.ConversationUser;
import com.watyouface.entity.User;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.ConversationUserRepository;

@Service
public class ConversationService {

    private final ConversationRepository convRepo;
    private final ConversationUserRepository convUserRepo;
    private final UserRepository userRepo;

    public ConversationService(
            ConversationRepository convRepo,
            ConversationUserRepository convUserRepo,
            UserRepository userRepo
    ) {
        this.convRepo = convRepo;
        this.convUserRepo = convUserRepo;
        this.userRepo = userRepo;
    }

    /**
     * 🔹 Crée ou récupère une conversation privée entre deux utilisateurs
     */
    @Transactional
    public Conversation getOrCreatePrivate(Long userId1, Long userId2) {
        // Vérifie si une conversation privée entre les deux existe déjà
        Optional<Conversation> existing = convRepo.findPrivateBetween(userId1, userId2);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Sinon, créer une nouvelle conversation
        Conversation conv = new Conversation();
        conv.setGroup(false);
        conv.setTitle(null); // pas de titre pour un chat privé
        conv = convRepo.save(conv);

        // Créer les liens participants
        List<User> users = userRepo.findAllById(List.of(userId1, userId2));
        for (User u : users) {
            ConversationUser cu = new ConversationUser();
            cu.setConversation(conv);
            cu.setUser(u);
            convUserRepo.save(cu);
        }

        return conv;
    }

    /**
     * 🔹 Crée une conversation de groupe
     */
    @Transactional
    public Conversation createGroup(String title, List<Long> participantIds) {
        Conversation conv = new Conversation();
        conv.setGroup(true);
        conv.setTitle(title);
        conv = convRepo.save(conv);

        List<User> users = userRepo.findAllById(participantIds);
        for (User u : users) {
            ConversationUser cu = new ConversationUser();
            cu.setConversation(conv);
            cu.setUser(u);
            convUserRepo.save(cu);
        }

        return conv;
    }

    /**
     * 🔹 Récupère toutes les conversations d’un utilisateur
     */
    public List<Conversation> findByUser(Long userId) {
        return convRepo.findByUserId(userId);
    }
}
