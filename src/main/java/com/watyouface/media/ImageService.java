package com.watyouface.media;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class ImageService {

    private final MediaStorageService storage;

    public ImageService(MediaStorageService storage) {
        this.storage = storage;
    }

    /** Sauvegarde image d'un post (max 1080px de large) */
    public String savePostImage(MultipartFile file, Long postId) throws IOException {

        String filename = "post_" + postId + "_" + System.currentTimeMillis() + ".jpg";
        String relativePath = "posts/" + filename;

        String outputPath = storage.resolvePath(relativePath);

        // ✅ Lire l'image pour calculer une hauteur valide
        BufferedImage img = ImageIO.read(file.getInputStream());
        if (img == null) {
            throw new IllegalArgumentException("Fichier image invalide (non lisible)");
        }

        int w = img.getWidth();
        int h = img.getHeight();
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Dimensions image invalides");
        }

        int targetW = Math.min(1080, w);
        int targetH = Math.max(1, (int) Math.round(h * (targetW / (double) w)));

        Thumbnails.of(img)
                .size(targetW, targetH)  // ✅ hauteur > 0
                .outputFormat("jpg")
                .toFile(outputPath);

        return storage.publicUrl(relativePath); // "/media/posts/<file>.jpg"
    }
}