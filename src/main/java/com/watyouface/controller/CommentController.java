package com.watyouface.controller;

import com.watyouface.entity.Comment;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    // ✅ Nouvelle version qui gère { postId, content } et ajoute automatiquement l’auteur
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> body, Principal principal) {
        try {
            // Récupère les champs du JSON
            Long postId = Long.valueOf(body.get("postId").toString());
            String content = body.get("content").toString();

            // Vérifie que le post existe
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // 🔹 Récupère le user depuis le token JWT
            String username = principal.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + username));

            // Crée le commentaire
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setPost(postOpt.get());
            comment.setAuthor(user); // ✅ essentiel

            Comment saved = commentService.createComment(comment);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l’ajout du commentaire : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
