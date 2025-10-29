// src/main/java/com/watyouface/controller/PostController.java

package com.watyouface.controller;

import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.service.PostService;
import com.watyouface.service.UserService;
import com.watyouface.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔑 Création de post avec fichier
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request) {

        // 🔐 Authentification
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String jwt = token.substring(7);

        if (!jwtUtil.validateToken(jwt)) {
            return ResponseEntity.status(401).build();
        }

        String username = jwtUtil.extractUsername(jwt);
        User author = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // 📝 Créer le post
        Post post = new Post();
        post.setContent(content);
        post.setAuthor(author);

        // 🖼️ Gérer le fichier
        if (file != null && !file.isEmpty()) {
            try {
                String filePath = postService.saveMediaFile(file);
                if (file.getContentType().startsWith("image/")) {
                    post.setImageUrl(filePath);
                } else if (file.getContentType().startsWith("video/")) {
                    post.setVideoUrl(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).build();
            }
        }

        Post createdPost = postService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}