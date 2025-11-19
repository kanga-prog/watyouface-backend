package com.watyouface.media;

import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.entity.VideoShare;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import com.watyouface.repository.VideoShareRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class VideoService {

    private final MediaStorageService storage;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final VideoShareRepository videoShareRepository;

    public VideoService(MediaStorageService storage,
                        VideoRepository videoRepository,
                        UserRepository userRepository,
                        VideoShareRepository videoShareRepository) {
        this.storage = storage;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.videoShareRepository = videoShareRepository;
    }

    // 🔹 Sauvegarde vidéo du post (convertie 720p + bitrate réduit)
    public String savePostVideo(MultipartFile file, Long postId) throws IOException, InterruptedException {
        String rawPath = storage.resolvePath("videos/post_" + postId + "_raw.mp4");
        String outputRelative = "videos/post_" + postId + ".mp4";
        String outputPath = storage.resolvePath(outputRelative);

        File originalFile = new File(rawPath);
        file.transferTo(originalFile);

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", originalFile.getAbsolutePath(),
                "-vf", "scale=720:-1",
                "-b:v", "1M",
                "-y",
                outputPath
        );
        Process process = pb.start();
        process.waitFor();

        return "/" + outputRelative;
    }

    // 🔹 Gestion CRUD vidéo
    public List<Video> getAllVideos() { 
        return videoRepository.findAll(); 
    }

    public Video createVideo(Video video) { 
        return videoRepository.save(video); 
    }

    public void deleteVideo(Long id) { 
        videoRepository.deleteById(id); 
    }

    // 🔹 Partage réel d'une vidéo
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

    public List<VideoShare> getSharedVideosForUser(Long receiverId) {
        return videoShareRepository.findByReceiverId(receiverId);
    }

    public List<VideoShare> getVideosSharedByUser(Long senderId) {
        return videoShareRepository.findBySenderId(senderId);
    }
}
