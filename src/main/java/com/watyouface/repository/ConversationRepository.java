package com.watyouface.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.watyouface.entity.Conversation;
import java.util.*;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // 🔹 Trouver une conversation privée entre deux utilisateurs
    @Query("""
        SELECT c FROM Conversation c
        JOIN c.participants cu1
        JOIN c.participants cu2
        WHERE cu1.user.id = :userId1
          AND cu2.user.id = :userId2
          AND c.isGroup = false
    """)
    Optional<Conversation> findPrivateBetween(Long userId1, Long userId2);

    // 🔹 Lister toutes les conversations d’un utilisateur (non paginée)
    @Query("""
        SELECT DISTINCT c FROM Conversation c
        JOIN c.participants cu
        WHERE cu.user.id = :userId
        ORDER BY c.createdAt DESC
    """)
    List<Conversation> findByUserId(Long userId);

    // 🔹 Lister les conversations avec pagination
    @Query("""
        SELECT DISTINCT c FROM Conversation c
        JOIN c.participants cu
        WHERE cu.user.id = :userId
        ORDER BY c.createdAt DESC
    """)
    Page<Conversation> findByUser(Long userId, Pageable pageable);
}
