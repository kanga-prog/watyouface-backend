package com.watyouface.service;

import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.entity.VideoShare;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import com.watyouface.repository.VideoShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private final VideoRepository videoRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final VideoShareRepository videoShareRepository;

    public VideoService(VideoRepository videoRepository,UserRepository userRepository,
                        VideoShareRepository videoShareRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.videoShareRepository = videoShareRepository;
    }

    public List<Video> getAllVideos() { return videoRepository.findAll(); }

    public Video createVideo(Video video) { return videoRepository.save(video); }

    public void deleteVideo(Long id) { videoRepository.deleteById(id); }
    //  🔹 Partage réel d'une vidéo
    public VideoShare shareVideo(Long videoId, Long senderId, Long receiverId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Utilisateur expéditeur non trouvé"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Utilisateur destinataire non trouvé"));

        VideoShare videoShare = new VideoShare(video, sender, receiver);
        return videoShareRepository.save(videoShare);
    }

    //  🔹 Récupérer toutes les vidéos partagées avec un utilisateur
    public List<VideoShare> getSharedVideosForUser(Long receiverId) {
        return videoShareRepository.findByReceiverId(receiverId);
    }

    //  🔹 Récupérer toutes les vidéos partagées par un utilisateur
    public List<VideoShare> getVideosSharedByUser(Long senderId) {
        return videoShareRepository.findBySenderId(senderId);
    }
}

