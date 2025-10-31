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
@CrossOrigin(origins = "*") // ✅ Autoriser le frontend à accéder depuis localhost ou IP
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    // ✅ 1. Récupérer tous les commentaires
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    // ✅ 2. Récupérer les commentaires d’un post spécifique
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // ✅ 3. Ajouter un commentaire à un post
    // Ton frontend appelle POST /api/comments avec { postId, content }
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Comment comment) {
        if (comment.getPost() == null || comment.getPost().getId() == null) {
            return ResponseEntity.badRequest().body("Le postId est requis.");
        }

        Optional<Post> postOpt = postRepository.findById(comment.getPost().getId());
        if (postOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        comment.setPost(postOpt.get());
        Comment saved = commentService.createComment(comment);
        return ResponseEntity.ok(saved);
    }

    // ✅ 4. Supprimer un commentaire
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
