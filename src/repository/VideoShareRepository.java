package com.watyouface.repository;

import com.watyouface.entity.VideoShare;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoShareRepository extends JpaRepository<VideoShare, Long> {

    // Récupérer toutes les vidéos partagées avec un utilisateur
    List<VideoShare> findByReceiverId(Long receiverId);

    // Récupérer toutes les vidéos envoyées par un utilisateur
    List<VideoShare> findBySenderId(Long senderId);
}
