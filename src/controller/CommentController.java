package com.watyouface.controller;

import com.watyouface.entity.Comment;
import com.watyouface.entity.Post;
import com.watyouface.repository.PostRepository;
import com.watyouface.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }

    // 🔥 Nouveau : Ajouter un commentaire à un post
    @PostMapping("/post/{postId}")
    public ResponseEntity<?> addCommentToPost(@PathVariable Long postId, @RequestBody Comment comment) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        comment.setPost(postOpt.get());
        Comment savedComment = commentService.createComment(comment);
        return ResponseEntity.ok(savedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
