package com.watyouface.controller;

import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.security.Authz;
import com.watyouface.service.PostService;
import com.watyouface.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private Authz authz;

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
            @RequestPart(required = false) MultipartFile file
    ) {
        try {
            Long userId = authz.me();

            User author = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Post post = new Post();
            post.setContent(content);
            post.setAuthor(author);

            if (file != null && !file.isEmpty()) {
                String filePath = postService.saveMediaFile(file);

                if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
                    post.setImageUrl(filePath);
                } else if (file.getContentType() != null && file.getContentType().startsWith("video/")) {
                    post.setVideoUrl(filePath);
                }
            }

            Post createdPost = postService.createPost(post);

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

        } catch (SecurityException e) {
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // ---------------------------------------------------------
    // DELETE POST
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            Long currentUserId = authz.me();
            boolean isAdmin = authz.isAdmin();

            postService.deletePostAs(id, currentUserId, isAdmin);
            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {
            return ResponseEntity.status(401).body("Non authentifié");
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }
}
