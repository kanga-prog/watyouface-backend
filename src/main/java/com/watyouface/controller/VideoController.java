package com.watyouface.controller;

import com.watyouface.entity.Video;
import com.watyouface.entity.VideoShare;
import com.watyouface.media.VideoService;
import com.watyouface.security.Authz;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final Authz authz;

    public VideoController(VideoService videoService, Authz authz) {
        this.videoService = videoService;
        this.authz = authz;
    }

    // 🔹 Créer une vidéo
    @PostMapping
    public ResponseEntity<Video> createVideo(@RequestBody Video video) {
        authz.me(); // exiger auth (si voulu)
        return ResponseEntity.ok(videoService.createVideo(video));
    }

    // 🔹 Récupérer toutes les vidéos
    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    // 🔹 Récupérer une vidéo par ID
    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        return videoService.getAllVideos().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Supprimer une vidéo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        authz.me(); // ou owner/admin si tu as un owner sur Video (idéalement à faire côté service)
        videoService.deleteVideo(id);
        return ResponseEntity.ok().build();
    }

    // 🔹 Partager une vidéo (sender = user connecté)
    @PostMapping("/share")
    public ResponseEntity<VideoShare> shareVideo(@RequestBody Map<String, Long> request) {
        Long me = authz.me();

        Long videoId = request.get("videoId");
        Long receiverId = request.get("receiverId");

        return ResponseEntity.ok(videoService.shareVideo(videoId, me, receiverId));
    }

    // 🔹 Récupérer les vidéos partagées avec moi (ou admin)
    @GetMapping("/shared-with/{userId}")
    public ResponseEntity<List<VideoShare>> getSharedWithUser(@PathVariable Long userId) {
        authz.ownerOrAdmin(userId);
        return ResponseEntity.ok(videoService.getSharedVideosForUser(userId));
    }

    // 🔹 Récupérer les vidéos partagées par moi (ou admin)
    @GetMapping("/shared-by/{userId}")
    public ResponseEntity<List<VideoShare>> getSharedByUser(@PathVariable Long userId) {
        authz.ownerOrAdmin(userId);
        return ResponseEntity.ok(videoService.getVideosSharedByUser(userId));
    }

    // 🔹 Mettre à jour le titre d'une vidéo
    @PatchMapping("/{id}")
    public ResponseEntity<Video> updateVideoTitle(@PathVariable Long id, @RequestBody Map<String, String> request) {
        authz.me(); // ou owner/admin si tu as un owner sur Video (à faire côté service)
        String newTitle = request.get("title");

        Video video = videoService.getAllVideos().stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vidéo non trouvée"));

        video.setTitle(newTitle);
        return ResponseEntity.ok(videoService.createVideo(video));
    }

    // 🔹 Rechercher par titre (partiel)
    @GetMapping("/search")
    public ResponseEntity<List<Video>> searchByTitle(@RequestParam String title) {
        List<Video> results = videoService.getAllVideos().stream()
                .filter(v -> v.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
        return ResponseEntity.ok(results);
    }
}
