package com.watyouface.media;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class AvatarService {

    private final MediaStorageService storage;

    private static final Set<String> ALLOWED = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );

    public AvatarService(MediaStorageService storage) {
        this.storage = storage;
    }

    public String saveAvatar(MultipartFile file, Long userId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier avatar manquant ou vide");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new IllegalArgumentException("Type de fichier non autorisé: " + contentType);
        }

        String ext = switch (contentType) {
            case MediaType.IMAGE_JPEG_VALUE -> ".jpg";
            case MediaType.IMAGE_PNG_VALUE -> ".png";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };

        String filename = "avatar_" + userId + "_" + UUID.randomUUID() + ext;
        String relativePath = "avatars/" + filename;

        // chemin disque absolu (créé si besoin)
        String outputPath = storage.resolvePath(relativePath);

        // copie vers le disque
        Path target = Paths.get(outputPath).normalize();
        Files.copy(file.getInputStream(), target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // ✅ URL publique unique
        return storage.publicUrl(relativePath); // "/media/avatars/<filename>"
    }
}