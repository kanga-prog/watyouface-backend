package com.watyouface.media;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MediaStorageService {

    // ✅ Root disque unique (absolu) — aligné avec WebConfig
    private final Path baseDir = Paths.get(System.getProperty("user.dir"), "media")
            .toAbsolutePath().normalize();

    /**
     * Résout un chemin disque complet sous media/ et crée le dossier parent.
     * Exemple:
     *  relativePath = "posts/post_12.jpg"
     *  => "/.../watyouface/media/posts/post_12.jpg"
     */
    public String resolvePath(String relativePath) {
        String clean = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

        Path fullPath = baseDir.resolve(clean).normalize();

        // sécurité: interdit de sortir de media/
        if (!fullPath.startsWith(baseDir)) {
            throw new SecurityException("Chemin invalide");
        }

        File file = fullPath.toFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        return file.getAbsolutePath();
    }

    /**
     * Retourne l'URL publique correspondante.
     * Exemple: "posts/post_12.jpg" => "/media/posts/post_12.jpg"
     */
    public String publicUrl(String relativePath) {
        String clean = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        return "/media/" + clean;
    }

    // optionnel (debug)
    public String getBaseDir() {
        return baseDir.toString() + File.separator;
    }
}