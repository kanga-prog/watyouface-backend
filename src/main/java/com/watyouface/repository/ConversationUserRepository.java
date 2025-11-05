package com.watyouface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.watyouface.entity.ConversationUser;

public interface ConversationUserRepository extends JpaRepository<ConversationUser, Long> {
}
