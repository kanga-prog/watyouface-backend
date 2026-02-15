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

    // ---------------------------------------------------------
    // GET ALL POSTS
    // ---------------------------------------------------------
    @GetMapping
    public List<Map<String, Object>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPostsOrderedByDateDesc();

            return posts.stream().map(post -> {
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
                    authorDto.put("id", 0L);
                    authorDto.put("username", "Anonyme");
                    authorDto.put("avatarUrl", null);
                }

                dto.put("author", authorDto);
                dto.put("likeCount", post.getLikes() != null ? post.getLikes().size() : 0);
                dto.put("commentCount", post.getComments() != null ? post.getComments().size() : 0);

                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ---------------------------------------------------------
    // GET POST BY ID
    // ---------------------------------------------------------
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
                authorDto.put("id", 0L);
                authorDto.put("username", "Anonyme");
                authorDto.put("avatarUrl", null);
            }

            dto.put("author", authorDto);
            dto.put("likeCount", post.getLikes() != null ? post.getLikes().size() : 0);
            dto.put("commentCount", post.getComments() != null ? post.getComments().size() : 0);

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // ---------------------------------------------------------
    // CREATE POST
    // ---------------------------------------------------------
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestPart(required = false) String content,
            @RequestPart(required = false) MultipartFile file,
            HttpServletRequest request) {

        try {
            // 1. JWT extraction
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Long userId = jwtUtil.extractUserId(token);

            User author = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // 2. Build post object
            Post post = new Post();
            post.setContent(content);
            post.setAuthor(author);

            // 3. Handle media upload
            if (file != null && !file.isEmpty()) {
                String filePath = postService.saveMediaFile(file);

                if (file.getContentType().startsWith("image/")) {
                    post.setImageUrl(filePath);
                } else if (file.getContentType().startsWith("video/")) {
                    post.setVideoUrl(filePath);
                }
            }

            // 4. Save post
            Post createdPost = postService.createPost(post);

            // 5. Build response DTO
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", createdPost.getId());
            dto.put("content", createdPost.getContent());
            dto.put("imageUrl", createdPost.getImageUrl());
            dto.put("videoUrl", createdPost.getVideoUrl());
            dto.put("createdAt", createdPost.getCreatedAt());

            Map<String, Object> authorDto = new HashMap<>();
            authorDto.put("id", author.getId());
            authorDto.put("username", author.getUsername());
            authorDto.put("avatarUrl", author.getAvatarUrl());
            dto.put("author", authorDto);

            dto.put("likeCount", 0);
            dto.put("commentCount", 0);

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // ---------------------------------------------------------
    // DELETE POST
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Token manquant");
            }

            Long currentUserId = jwtUtil.getUserIdFromHeader(authHeader);
            boolean isAdmin = "ADMIN".equals(jwtUtil.getRoleFromHeader(authHeader));

            postService.deletePostAs(id, currentUserId, isAdmin);
            return ResponseEntity.noContent().build();

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }

}