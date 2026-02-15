// src/main/java/com/watyouface/service/PostService.java

package com.watyouface.service;

import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.repository.PostRepository;
import com.watyouface.media.ImageService;
import com.watyouface.media.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private VideoService videoService;

    // Dossier d'upload par défaut (utilisé pour saveMediaFile)
    private static final String UPLOAD_DIR = "uploads/";

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    /** Retourne tous les posts */
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    /** Retourne un post par son ID */
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    /** Supprime un post par son ID */
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    /** Crée un post sans fichier média */
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    /** Crée un post avec un fichier média (image ou vidéo) */
    public Post createPost(Post post, MultipartFile file) {

        // 🔑 Étape 1 : sauvegarder le post pour générer l'ID
        Post saved = postRepository.save(post);

        // 🔑 Étape 2 : traiter le média
        if (file != null && !file.isEmpty()) {
            try {
                String contentType = file.getContentType();

                if (contentType != null) {
                    if (contentType.startsWith("image/")) {
                        String url = imageService.savePostImage(file, saved.getId());
                        saved.setImageUrl(url);

                    } else if (contentType.startsWith("video/")) {
                        String url = videoService.savePostVideo(file, saved.getId());
                        saved.setVideoUrl(url);
                    }
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 🔑 Étape 3 : mettre à jour le post avec l'URL du média
        return postRepository.save(saved);
    }

    /** Sauvegarde un fichier média générique (uploads/) */
    public String saveMediaFile(MultipartFile file) throws IOException {
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(UPLOAD_DIR);
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        // Générer un nom unique
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "media_" + timestamp + extension;
        java.nio.file.Path filePath = uploadPath.resolve(filename);

        java.nio.file.Files.write(filePath, file.getBytes());
        return "/" + UPLOAD_DIR + filename;
    }

    /** Récupère tous les posts du plus récent au plus ancien */
    public List<Post> getAllPostsOrderedByDateDesc() {
        return postRepository.findAllOrderedByDateDesc();
    }
    public Post getPostOrThrow(Long id) {
    return postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post introuvable"));
    }

    /**
     * ✅ Supprimer un post avec contrôle d’accès
     * - ADMIN peut tout supprimer
     * - USER peut supprimer uniquement ses posts
     */
    public void deletePostAs(Long postId, Long currentUserId, boolean isAdmin) {
        Post post = getPostOrThrow(postId);

        Long authorId = post.getAuthor() != null ? post.getAuthor().getId() : null;

        if (!isAdmin) {
            if (authorId == null || !authorId.equals(currentUserId)) {
                throw new AccessDeniedException("Interdit : vous ne pouvez supprimer que vos posts.");
            }
        }

        postRepository.deleteById(postId);
    }
}
