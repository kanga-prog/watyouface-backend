package com.watyouface.service;

import com.watyouface.entity.Message;
import com.watyouface.entity.Conversation;
import com.watyouface.entity.User;
import com.watyouface.repository.MessageRepository;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.dto.MessageDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import com.watyouface.entity.ConversationUser;

@Service
public class MessageService {
    private final MessageRepository messageRepo;
    private final ConversationRepository convRepo;
    private final UserRepository userRepo;

    public MessageService(MessageRepository messageRepo, ConversationRepository convRepo, UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.convRepo = convRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public List<Message> sendMessage(Long conversationId, Long senderId, String content) {

        Conversation conv = convRepo.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        List<Message> result = new ArrayList<>();

        // 🟢 MODE GROUPE
        if (conv.getParticipants().size() > 2) {
            for (ConversationUser cu : conv.getParticipants()) {
                User participant = cu.getUser();

                if (!participant.getId().equals(senderId)) {
                    Message m = new Message();
                    m.setConversation(conv);
                    m.setSender(sender);
                    m.setReceiver(participant);
                    m.setContent(content);
                    m.setSentAt(Instant.now());

                    result.add(m);
                }
            }
            return messageRepo.saveAll(result);
        }

        // 🟢 MODE PRIVÉ (2 personnes)
        User receiver = conv.getParticipants().stream()
                .map(ConversationUser::getUser)
                .filter(u -> !u.getId().equals(senderId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Private chat must have 2 users"));

        Message m = new Message();
        m.setConversation(conv);
        m.setSender(sender);
        m.setReceiver(receiver);
        m.setContent(content);
        m.setSentAt(Instant.now());

        return List.of(messageRepo.save(m));
    }



    public Page<Message> fetchMessages(Long conversationId, int page, int size) {
        return messageRepo.findByConversationIdOrderBySentAtDesc(conversationId, PageRequest.of(page, size));
    }

    // 🔹 Nouvelle méthode pour l’API REST front
    public List<MessageDTO> findByConversation(Long conversationId) {
        return messageRepo.findByConversationIdOrderBySentAtDesc(conversationId) 
                          .stream()
                          .map(MessageDTO::new)
                          .collect(Collectors.toList());
    }
}
