package com.watyouface.service;

import com.watyouface.entity.Conversation;
import com.watyouface.entity.ConversationUser;
import com.watyouface.entity.Message;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.MessageRepository;
import com.watyouface.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;

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
    public Conversation createOneToOneConversation(Long a, Long b) {
        return convRepo.findOneToOneByUsers(a, b)
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setGroup(false);
                    convRepo.save(c);

                    ConversationUser cuA = new ConversationUser();
                    cuA.setConversation(c);
                    cuA.setUser(userRepo.getReferenceById(a));

                    ConversationUser cuB = new ConversationUser();
                    cuB.setConversation(c);
                    cuB.setUser(userRepo.getReferenceById(b));

                    c.setParticipants(Set.of(cuA, cuB));
                    return convRepo.save(c);
                });
    }

    public Message sendMessage(Long conversationId, Long senderId, String content) {
        Conversation conv = convRepo.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        boolean isParticipant = conv.getParticipants()
                .stream()
                .anyMatch(p -> p.getUser().getId().equals(senderId));

        if (!isParticipant) throw new AccessDeniedException("User not participant in this conversation");

        Message m = new Message();
        m.setConversation(conv);
        m.setSender(userRepo.getReferenceById(senderId));
        m.setContent(content);
        m.setSentAt(Instant.now());
        return messageRepo.save(m);
    }

    public Page<Message> fetchMessages(Long conversationId, int page, int size) {
        return messageRepo.findByConversationIdOrderBySentAtDesc(conversationId, PageRequest.of(page, size));
    }
}
