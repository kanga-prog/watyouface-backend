package com.watyouface.media;

import com.watyouface.exception.MediaUploadException;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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
            MediaType.IMAGE_PNG_VALUE
    );
    private static final Map<String, String> MIME_TO_FORMAT = Map.of(
            MediaType.IMAGE_JPEG_VALUE, "jpeg",
            MediaType.IMAGE_PNG_VALUE, "png"
    );

    public AvatarService(MediaStorageService storage) {
        this.storage = storage;
    }

    /**
     * Sauvegarde un avatar: crop center + resize 256x256 + sortie JPG (compat max).
     * Retourne une URL publique "/media/avatars/<file>.jpg".
     */
    public String saveAvatar(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw MediaUploadException.badRequest("Fichier avatar manquant ou vide");
        }
        if (file.getSize() > MAX_BYTES) {
            throw MediaUploadException.payloadTooLarge("Avatar trop volumineux (max 5MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw MediaUploadException.unsupportedMediaType("Type de fichier non autorisé");
        }

        BufferedImage image = decodeAndVerifyMime(file);

        String filename = "avatar_" + userId + "_" + UUID.randomUUID() + ".jpg";
        String relative = "avatars/" + filename;
        String outputPath = storage.resolvePath(relative);

        // ✅ crop + resize stable
        try {
            Thumbnails.of(image)
                    .size(AVATAR_SIZE, AVATAR_SIZE)
                    .crop(Positions.CENTER)
                    .outputFormat("jpg")
                    .outputQuality(0.9)
                    .toFile(outputPath);
        } catch (IOException | RuntimeException e) {
            throw MediaUploadException.storageFailure(e);
        }

        return storage.publicUrl(relative);
    }

    private BufferedImage decodeAndVerifyMime(MultipartFile file) {
        try (ImageInputStream stream = ImageIO.createImageInputStream(file.getInputStream())) {
            if (stream == null) {
                throw MediaUploadException.unprocessable("Avatar invalide ou format non lisible");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                throw MediaUploadException.unprocessable("Avatar invalide ou format non lisible");
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                String actualFormat = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!actualFormat.equals(MIME_TO_FORMAT.get(file.getContentType()))) {
                    throw MediaUploadException.unprocessable("Le contenu ne correspond pas au type MIME déclaré");
                }
                BufferedImage image = reader.read(0);
                if (image == null) {
                    throw MediaUploadException.unprocessable("Avatar invalide ou format non lisible");
                }
                return image;
            } finally {
                reader.dispose();
            }
        } catch (MediaUploadException e) {
            throw e;
        } catch (IOException | RuntimeException e) {
            throw MediaUploadException.unprocessable("Avatar invalide ou format non lisible");
        }
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
