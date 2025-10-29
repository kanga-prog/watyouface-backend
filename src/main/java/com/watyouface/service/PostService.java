// src/main/java/com/watyouface/service/PostService.java

package com.watyouface.service;

import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService; // ← ajouté

    // Dossier d'upload
    private static final String UPLOAD_DIR = "uploads/";

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService; // ← injection
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // 🔑 Nouvelle méthode : sauvegarde un fichier média
    public String saveMediaFile(MultipartFile file) throws IOException {
        // Créer le dossier s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom unique
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = "media_" + timestamp + extension;
        Path filePath = uploadPath.resolve(filename);

        // Sauvegarder le fichier
        Files.write(filePath, file.getBytes());

        // Retourner le chemin relatif (ex: "/uploads/media_20251028123456789.jpg")
        return "/" + UPLOAD_DIR + filename;
    }
}