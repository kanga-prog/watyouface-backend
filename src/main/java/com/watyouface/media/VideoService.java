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

        // ✅ Resize intelligent :
        // - garde le ratio
        // - limite à 720p max
        // - force dimensions paires (nécessaire pour H.264)
        String scaleFilter = "scale='min(1280,iw)':'-2'";

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", originalFile.getAbsolutePath(),
                "-vf", scaleFilter,
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "23",
                "-c:a", "aac",
                "-b:a", "128k",
                "-movflags", "+faststart",
                outputPath
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();
        String log = new String(process.getInputStream().readAllBytes());
        int code = process.waitFor();

        if (code != 0) {
            throw new RuntimeException("ffmpeg a échoué (code " + code + "):\n" + log);
        }

        // optionnel: supprimer le raw si tout OK
        // originalFile.delete();

        return storage.publicUrl(outputRelative); // "/media/videos/post_<id>.mp4"
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
