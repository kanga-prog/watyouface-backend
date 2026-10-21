package com.watyouface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.watyouface.entity.ConversationUser;
import java.util.List;

public interface ConversationUserRepository extends JpaRepository<ConversationUser, Long> {
    List<ConversationUser> findByUser_Id(Long userId);
    long countByConversation_Id(Long conversationId);
}
