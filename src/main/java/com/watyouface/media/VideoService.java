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
import java.util.concurrent.TimeUnit;

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

        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        Process process = pb.start();
        boolean completed = process.waitFor(120, TimeUnit.SECONDS);
        if (!completed) {
            process.destroyForcibly();
            originalFile.delete();
            throw new IllegalArgumentException("Traitement vidéo interrompu (délai dépassé)");
        }
        int code = process.exitValue();

        if (code != 0) {
            originalFile.delete();
            throw new IllegalArgumentException("Vidéo invalide ou transcodage impossible");
        }

        originalFile.delete();

        return storage.publicUrl(outputRelative); // "/media/videos/post_<id>.mp4"
    }

    // 🔹 Gestion CRUD vidéo
    public List<Video> getAllVideos() { 
        return videoRepository.findAll(); 
    }

    public Video createVideo(Video video) { 
        return videoRepository.save(video); 
    }

    public Video createVideoAs(Video video, Long uploaderId) {
        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        video.setUploader(uploader);
        return videoRepository.save(video);
    }

    public Video getVideoOrThrow(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vidéo non trouvée"));
    }

    public Video updateTitleAs(Long id, String title, Long actorId, boolean isAdmin) {
        Video video = getVideoOrThrow(id);
        assertOwnerOrAdmin(video, actorId, isAdmin);
        video.setTitle(title.trim());
        return videoRepository.save(video);
    }

    public void deleteVideoAs(Long id, Long actorId, boolean isAdmin) {
        Video video = getVideoOrThrow(id);
        assertOwnerOrAdmin(video, actorId, isAdmin);
        videoRepository.delete(video);
    }

    private void assertOwnerOrAdmin(Video video, Long actorId, boolean isAdmin) {
        Long uploaderId = video.getUploader() != null ? video.getUploader().getId() : null;
        if (!isAdmin && (uploaderId == null || !uploaderId.equals(actorId))) {
            throw new SecurityException("Interdit");
        }
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
