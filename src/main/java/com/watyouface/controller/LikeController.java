package com.watyouface.controller;

import com.watyouface.entity.Like;
import com.watyouface.entity.Post;
import com.watyouface.entity.Video;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.VideoRepository;
import com.watyouface.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private VideoRepository videoRepository;

    @GetMapping
    public List<Like> getAllLikes() {
        return likeService.getAllLikes();
    }

    // 🔹 Toggle like pour post ou video
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleLike(@RequestBody Map<String, Long> payload, Principal principal) {
        Long postId = payload.get("postId");
        Long videoId = payload.get("videoId");

        Post post = null;
        Video video = null;

        if (postId != null) {
            post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
        }

        if (videoId != null) {
            video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found"));
        }

        if (post == null && video == null) {
            return ResponseEntity.badRequest().body("postId ou videoId manquant");
        }

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId;
        try {
            userId = Long.valueOf(principal.getName());
        } catch (NumberFormatException invalidPrincipal) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean liked = likeService.toggleLike(post, video, userId);

        if (liked) {
            return ResponseEntity.ok("Like ajouté");
        } else {
            return ResponseEntity.ok("Like retiré");
        }
    }

    // Legacy routes are intentionally disabled: they accepted a client-controlled Like body.
    @PostMapping("/post/{postId}")
    public ResponseEntity<?> addLikeToPost(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.GONE).body(Map.of("error", "Endpoint legacy désactivé ; utilisez /api/likes/toggle"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.GONE).build();
    }
}
