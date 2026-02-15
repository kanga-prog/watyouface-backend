package com.watyouface.controller;

import com.watyouface.entity.Comment;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.repository.PostRepository;
import com.watyouface.security.JwtUtil;
import com.watyouface.service.CommentService;
import com.watyouface.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    // ✅ CREATE : auteur depuis JWT
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Token manquant");
            }

            Long currentUserId = jwtUtil.getUserIdFromHeader(authHeader);
            if (currentUserId == null) {
                return ResponseEntity.status(401).body("Token invalide");
            }

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

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l’ajout du commentaire : " + e.getMessage());
        }
    }

    // ✅ UPDATE : owner/admin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                          @RequestBody Map<String, Object> body,
                                          HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Token manquant");
            }

            Long currentUserId = jwtUtil.getUserIdFromHeader(authHeader);
            if (currentUserId == null) {
                return ResponseEntity.status(401).body("Token invalide");
            }

            boolean isAdmin = "ADMIN".equals(jwtUtil.getRoleFromHeader(authHeader));

            Object contentObj = body.get("content");
            if (contentObj == null) {
                return ResponseEntity.badRequest().body("content est requis");
            }

            String newContent = contentObj.toString();
            Comment updated = commentService.updateCommentAs(id, newContent, currentUserId, isAdmin);

            return ResponseEntity.ok(updated);

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
    public ResponseEntity<?> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Token manquant");
            }

            Long currentUserId = jwtUtil.getUserIdFromHeader(authHeader);
            if (currentUserId == null) {
                return ResponseEntity.status(401).body("Token invalide");
            }

            boolean isAdmin = "ADMIN".equals(jwtUtil.getRoleFromHeader(authHeader));

            commentService.deleteCommentAs(id, currentUserId, isAdmin);
            return ResponseEntity.noContent().build();

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }
}
