package com.watyouface.media;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class MarketplaceImageService {

    private final MediaStorageService storage;

    private static final long MAX_BYTES = 10L * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );

    public MarketplaceImageService(MediaStorageService storage) {
        this.storage = storage;
    }

    /**
     * Sauvegarde une image d’annonce marketplace dans media/marketplace/
     * et retourne l’URL publique /media/marketplace/<file>.jpg
     */
    public String saveListingImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image manquante ou vide");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Image trop volumineuse (max 10MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new IllegalArgumentException("Type non autorisé: " + contentType);
        }

        String filename = "listing_" + UUID.randomUUID() + ".jpg";
        String relative = "marketplace/" + filename;
        String outputPath = storage.resolvePath(relative);

        // ✅ compression “safe” en jpg (compat maxi)
        Thumbnails.of(file.getInputStream())
                .size(1400, 1400)          // limite (garde ratio)
                .outputFormat("jpg")
                .outputQuality(0.88)
                .toFile(outputPath);

        return storage.publicUrl(relative); // "/media/marketplace/<file>.jpg"
    }
}