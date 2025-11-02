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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔹 Récupérer tous les posts triés du plus récent au plus ancien
    @GetMapping
    public List<Map<String, Object>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPostsOrderedByDateDesc();
            return posts.stream().map(post -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", post.getId());
                dto.put("content", post.getContent() != null ? post.getContent() : "");
                dto.put("imageUrl", post.getImageUrl());
                dto.put("videoUrl", post.getVideoUrl());
                dto.put("createdAt", post.getCreatedAt());

                User author = post.getAuthor();
                Map<String, Object> authorDto = new HashMap<>();
                if (author != null) {
                    authorDto.put("id", author.getId() != null ? author.getId() : 0);
                    authorDto.put("username", author.getUsername() != null ? author.getUsername() : "Anonyme");
                    authorDto.put("avatarUrl", author.getAvatarUrl());
                } else {
                    authorDto.put("id", 0);
                    authorDto.put("username", "Anonyme");
                    authorDto.put("avatarUrl", null);
                }
                dto.put("author", authorDto);

                dto.put("likeCount", (post.getLikes() != null) ? post.getLikes().size() : 0);
                dto.put("commentCount", (post.getComments() != null) ? post.getComments().size() : 0);

                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 🔹 Récupérer un post par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable Long id) {
        try {
            Optional<Post> postOpt = postService.getPostById(id);
            if (postOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Post post = postOpt.get();
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", post.getId());
            dto.put("content", post.getContent());
            dto.put("imageUrl", post.getImageUrl());
            dto.put("videoUrl", post.getVideoUrl());
            dto.put("createdAt", post.getCreatedAt());

            User author = post.getAuthor();
            Map<String, Object> authorDto = new HashMap<>();
            if (author != null) {
                authorDto.put("id", author.getId());
                authorDto.put("username", author.getUsername());
                authorDto.put("avatarUrl", author.getAvatarUrl());
            } else {
                authorDto.put("id", 0);
                authorDto.put("username", "Anonyme");
                authorDto.put("avatarUrl", null);
            }
            dto.put("author", authorDto);

            dto.put("likeCount", (post.getLikes() != null) ? post.getLikes().size() : 0);
            dto.put("commentCount", (post.getComments() != null) ? post.getComments().size() : 0);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 🔹 Création d’un post avec contenu et/ou fichier
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile file,
            HttpServletRequest request) {

        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) return ResponseEntity.status(401).build();
            String jwt = token.substring(7);
            if (!jwtUtil.validateToken(jwt)) return ResponseEntity.status(401).build();

            String username = jwtUtil.extractUsername(jwt);
            User author = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Post post = new Post();
            post.setContent(content);
            post.setAuthor(author);

            if (file != null && !file.isEmpty()) {
                String filePath = postService.saveMediaFile(file);
                if (file.getContentType().startsWith("image/")) post.setImageUrl(filePath);
                else if (file.getContentType().startsWith("video/")) post.setVideoUrl(filePath);
            }

            Post createdPost = postService.createPost(post);

            Map<String, Object> dto = new HashMap<>();
            dto.put("id", createdPost.getId());
            dto.put("content", createdPost.getContent());
            dto.put("imageUrl", createdPost.getImageUrl());
            dto.put("videoUrl", createdPost.getVideoUrl());
            dto.put("createdAt", createdPost.getCreatedAt());

            Map<String, Object> authorDto = new HashMap<>();
            authorDto.put("id", createdPost.getAuthor().getId());
            authorDto.put("username", createdPost.getAuthor().getUsername());
            authorDto.put("avatarUrl", createdPost.getAuthor().getAvatarUrl());
            dto.put("author", authorDto);

            dto.put("likeCount", 0);
            dto.put("commentCount", 0);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // 🔹 Supprimer un post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}
