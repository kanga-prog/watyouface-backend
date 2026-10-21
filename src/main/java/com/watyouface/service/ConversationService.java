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

    private final ConversationRepository conversationRepository;
    private final ConversationUserRepository conversationUserRepository;
    private final UserRepository userRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationUserRepository conversationUserRepository,
            UserRepository userRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationUserRepository = conversationUserRepository;
        this.userRepository = userRepository;
    }

    /**
     * 🔹 Crée ou récupère une conversation privée entre deux utilisateurs
     */
    @Transactional
    public Conversation GetOrCreatePrivate(Long userId1, Long userId2) {
        Optional<Conversation> existing = conversationRepository.findPrivateBetween(userId1, userId2);
        if (existing.isPresent()) {
            return existing.get();
        }

        Conversation conversation = new Conversation();
        conversation.setGroup(false);
        conversation.setTitle(null);
        conversation = conversationRepository.save(conversation);

        List<User> users = List.of(
                activeUser(userId1),
                activeUser(userId2));
        for (User user : users) {
            ConversationUser cu = new ConversationUser();
            cu.setConversation(conversation);
            cu.setUser(user);
            conversationUserRepository.save(cu);
        }

        return conversation;
    }

    /**
     * 🔹 Crée une conversation de groupe
     */
    @Transactional
    public Conversation CreateGroup(String title, List<Long> participantIds) {
        Conversation conversation = new Conversation();
        conversation.setGroup(true);
        conversation.setTitle(title);
        conversation = conversationRepository.save(conversation);

        List<User> users = participantIds.stream().map(this::activeUser).toList();
        for (User user : users) {
            ConversationUser cu = new ConversationUser();
            cu.setConversation(conversation);
            cu.setUser(user);
            conversationUserRepository.save(cu);
        }

        return conversation;
    }

    /**
     * 🔹 Récupère toutes les conversations d’un utilisateur
     */
    public List<Conversation> FindByUser(Long userId) {
        return conversationRepository.findByUserId(userId);
    }

    private User activeUser(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable ou supprimé"));
    }
}
