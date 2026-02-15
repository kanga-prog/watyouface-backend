package com.watyouface.controller;

import com.watyouface.entity.Comment;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.repository.PostRepository;
import com.watyouface.security.Authz;
import com.watyouface.service.CommentService;
import com.watyouface.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private UserService userService;

    @Autowired
    private Authz authz;

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    // ✅ CREATE : auteur depuis SecurityContext
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> body) {
        try {
            Long currentUserId = authz.me();

            Object postIdObj = body.get("postId");
            Object contentObj = body.get("content");
            if (postIdObj == null || contentObj == null) {
                return ResponseEntity.badRequest().body("postId et content sont requis");
            }

            Long postId = Long.valueOf(postIdObj.toString());
            String content = contentObj.toString().trim();
            if (content.isEmpty()) {
                return ResponseEntity.badRequest().body("content ne doit pas être vide");
            }

            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Post introuvable");
            }

            User author = userService.getUserById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            Comment comment = new Comment();
            comment.setContent(content);
            comment.setPost(postOpt.get());
            comment.setAuthor(author);

            Comment saved = commentService.createComment(comment);
            return ResponseEntity.ok(saved);

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("Non authentifié");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l’ajout du commentaire : " + e.getMessage());
        }
    }

    // ✅ UPDATE : owner/admin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                          @RequestBody Map<String, Object> body) {
        try {
            Long currentUserId = authz.me();
            boolean isAdmin = authz.isAdmin();

            Object contentObj = body.get("content");
            if (contentObj == null) {
                return ResponseEntity.badRequest().body("content est requis");
            }

            String newContent = contentObj.toString();
            Comment updated = commentService.updateCommentAs(id, newContent, currentUserId, isAdmin);

            return ResponseEntity.ok(updated);

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("Non authentifié");
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }

    // ✅ DELETE : owner/admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            Long currentUserId = authz.me();
            boolean isAdmin = authz.isAdmin();

            commentService.deleteCommentAs(id, currentUserId, isAdmin);
            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("Non authentifié");
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }
}
