package com.watyouface.media;

import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.entity.VideoShare;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import com.watyouface.repository.VideoShareRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VideoService {

    private final MediaStorageService storage;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final VideoShareRepository videoShareRepository;

    @Autowired
    private MediaReferenceCleanupService mediaCleanup;

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
        File outputFile = new File(outputPath);
        boolean completedSuccessfully = false;
        try {
            file.transferTo(originalFile);

            // ✅ Resize intelligent :
            // - garde le ratio
            // - limite à 720p max
            // - force dimensions paires (nécessaire pour H.264)
            String scaleFilter = "scale='min(1280,iw)':'-2'";

            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-y", "-i", originalFile.getAbsolutePath(), "-vf", scaleFilter,
                    "-c:v", "libx264", "-preset", "veryfast", "-crf", "23",
                    "-c:a", "aac", "-b:a", "128k", "-movflags", "+faststart", outputPath
            );

            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            Process process = pb.start();
            boolean completed = process.waitFor(120, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw new IllegalArgumentException("Traitement vidéo interrompu (délai dépassé)");
            }
            if (process.exitValue() != 0) {
                throw new IllegalArgumentException("Vidéo invalide ou transcodage impossible");
            }
            completedSuccessfully = true;
            return storage.publicUrl(outputRelative);
        } finally {
            Files.deleteIfExists(originalFile.toPath());
            if (!completedSuccessfully) {
                Files.deleteIfExists(outputFile.toPath());
            }
        }
    }

    // 🔹 Gestion CRUD vidéo
    public List<Video> getAllVideos() { 
        return videoRepository.findAll(); 
    }

    public Video createVideo(Video video) { 
        return videoRepository.save(video); 
    }

    public Video createVideoAs(Video video, Long uploaderId) {
        User uploader = userRepository.findByIdAndDeletedAtIsNull(uploaderId)
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

    @Transactional
    public void deleteVideoAs(Long id, Long actorId, boolean isAdmin) {
        Video video = getVideoOrThrow(id);
        assertOwnerOrAdmin(video, actorId, isAdmin);
        String previousUrl = video.getUrl();
        videoRepository.delete(video);
        if (mediaCleanup != null && previousUrl != null && !previousUrl.isBlank()) {
            mediaCleanup.deleteIfUnreferencedAfterCommit(List.of(previousUrl));
        }
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
        User sender = userRepository.findByIdAndDeletedAtIsNull(senderId)
                .orElseThrow(() -> new RuntimeException("Utilisateur expéditeur non trouvé"));
        User receiver = userRepository.findByIdAndDeletedAtIsNull(receiverId)
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
