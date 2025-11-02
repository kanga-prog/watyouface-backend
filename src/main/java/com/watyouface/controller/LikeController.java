package com.watyouface.controller;

import com.watyouface.entity.Like;
import com.watyouface.entity.Post;
import com.watyouface.entity.Video;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.VideoRepository;
import com.watyouface.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

        String username = principal != null ? principal.getName() : "anonymous";
        boolean liked = likeService.toggleLike(post, video, username);

        if (liked) {
            return ResponseEntity.ok("✅ Liked by " + username);
        } else {
            return ResponseEntity.ok("❌ Like removed by " + username);
        }
    }

    // Ancien endpoint pour ajouter like directement (optionnel)
    @PostMapping("/post/{postId}")
    public ResponseEntity<?> addLikeToPost(@PathVariable Long postId, @RequestBody Like like) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        like.setPost(postOpt.get());
        Like savedLike = likeService.createLike(like);
        return ResponseEntity.ok(savedLike);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) {
        likeService.deleteLike(id);
        return ResponseEntity.noContent().build();
    }
}
