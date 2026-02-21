package com.watyouface.media;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class AvatarService {

    private final MediaStorageService storage;

    // tailles “verrouillées”
    private static final int AVATAR_SIZE = 256; // 256x256 carré

    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB

    private static final Set<String> ALLOWED = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );

    public AvatarService(MediaStorageService storage) {
        this.storage = storage;
    }

    /**
     * Sauvegarde un avatar: crop center + resize 256x256 + sortie JPG (compat max).
     * Retourne une URL publique "/media/avatars/<file>.jpg".
     */
    public String saveAvatar(MultipartFile file, Long userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier avatar manquant ou vide");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Avatar trop volumineux (max 5MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new IllegalArgumentException("Type de fichier non autorisé: " + contentType);
        }

        String filename = "avatar_" + userId + "_" + UUID.randomUUID() + ".jpg";
        String relative = "avatars/" + filename;
        String outputPath = storage.resolvePath(relative);

        // ✅ crop + resize stable
        Thumbnails.of(file.getInputStream())
                .size(AVATAR_SIZE, AVATAR_SIZE)
                .crop(Positions.CENTER)
                .outputFormat("jpg")
                .outputQuality(0.9)
                .toFile(outputPath);

        return storage.publicUrl(relative);
    }

    /**
     * Supprime un ancien avatar si c'est un fichier géré par l'app (sous /media/avatars/)
     */
    public void deleteIfManaged(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) return;
        if (!avatarUrl.startsWith("/media/avatars/")) return;
        if (avatarUrl.endsWith("/default.png") || avatarUrl.endsWith("/default.jpg")) return;

        String relative = avatarUrl.replaceFirst("^/media/", "");
        try {
            String abs = storage.resolvePath(relative);
            new File(abs).delete();
        } catch (Exception ignored) {
            // pas bloquant
        }
    }
}
