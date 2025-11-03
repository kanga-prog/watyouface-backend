package com.watyouface.repository;

import com.watyouface.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.user.id = :userId")
    Page<Conversation> findByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 " +
           "WHERE p1.user.id = :a AND p2.user.id = :b AND c.isGroup = false")
    Optional<Conversation> findOneToOneByUsers(@Param("a") Long a, @Param("b") Long b);
}
