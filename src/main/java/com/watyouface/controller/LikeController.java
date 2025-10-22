package com.watyouface.controller;

import com.watyouface.entity.Like;
import com.watyouface.entity.Post;
import com.watyouface.repository.PostRepository;
import com.watyouface.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Like> getAllLikes() {
        return likeService.getAllLikes();
    }

    // 🔥 Nouveau : Ajouter un like à un post
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
