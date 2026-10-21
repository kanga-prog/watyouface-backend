package com.watyouface.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

@Service
public class MediaStorageService {

    private static final Set<String> MANAGED_DIRECTORIES = Set.of("avatars", "posts", "marketplace", "videos");
    private final Path baseDir;

    /**
     * Production keeps the historic ${user.dir}/media default (/app/media in
     * Railway). A dedicated root makes filesystem lifecycle tests isolated.
     */
    public MediaStorageService(@Value("${app.media.base-dir:}") String configuredBaseDir) {
        this.baseDir = (configuredBaseDir == null || configuredBaseDir.isBlank()
                ? Paths.get(System.getProperty("user.dir"), "media")
                : Paths.get(configuredBaseDir))
                .toAbsolutePath().normalize();
    }

    /**
     * Résout un chemin disque complet sous media/ et crée le dossier parent.
     * Exemple: relativePath = "avatars/avatar_1.jpg" => "/.../media/avatars/avatar_1.jpg"
     */
    public String resolvePath(String relativePath) {
        Path fullPath = resolveRelative(relativePath);

        File file = fullPath.toFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        return file.getAbsolutePath();
    }

    /**
     * Retourne l'URL publique correspondante.
     * Exemple: "avatars/avatar_1.jpg" => "/media/avatars/avatar_1.jpg"
     */
    public String publicUrl(String relativePath) {
        String clean = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        return "/media/" + clean;
    }

    // utile en debug
    public String getBaseDir() {
        return baseDir.toString() + File.separator;
    }

    /** Root used by the reconciliation tool. Callers must never walk outside it. */
    public Path getBasePath() {
        return baseDir;
    }

    /**
     * Normalizes only public URLs owned by this application. It deliberately
     * rejects external URLs, absolute filesystem paths and traversal attempts.
     */
    public Optional<String> normalizeManagedPublicUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) return Optional.empty();
        String candidate = publicUrl.trim().replace('\\', '/');
        if (!candidate.startsWith("/media/")) return Optional.empty();
        String relative = candidate.substring("/media/".length());
        if (relative.isBlank() || relative.contains("//")) return Optional.empty();
        Path normalized;
        try {
            normalized = Paths.get(relative).normalize();
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
        if (normalized.isAbsolute() || normalized.startsWith("..") || normalized.getNameCount() < 2) {
            return Optional.empty();
        }
        String clean = normalized.toString().replace(File.separatorChar, '/');
        String directory = clean.substring(0, clean.indexOf('/'));
        if (!MANAGED_DIRECTORIES.contains(directory) || clean.contains("../")) return Optional.empty();
        return Optional.of("/media/" + clean);
    }

    public Optional<Path> managedPathForPublicUrl(String publicUrl) {
        return normalizeManagedPublicUrl(publicUrl)
                .map(url -> baseDir.resolve(url.substring("/media/".length())).normalize())
                .filter(path -> path.startsWith(baseDir));
    }

    public boolean deleteManagedRelativePath(String relativePath) {
        return managedPathForPublicUrl("/media/" + relativePath)
                .filter(path -> !Files.isSymbolicLink(path))
                .map(path -> {
                    try {
                        return Files.deleteIfExists(path);
                    } catch (IOException | SecurityException ignored) {
                        return false;
                    }
                })
                .orElse(false);
    }

    /**
     * Deletes only an application-managed media URL. External URLs, defaults,
     * traversal attempts and unknown directories are deliberately ignored.
     */
    public boolean deleteManagedPublicUrl(String publicUrl) {
        Optional<String> normalized = normalizeManagedPublicUrl(publicUrl);
        if (normalized.isEmpty()) return false;
        String relative = normalized.get().substring("/media/".length());
        if (relative.equals("avatars/default.png") || relative.equals("avatars/default.jpg")) {
            return false;
        }
        return deleteManagedRelativePath(relative);
    }

    private Path resolveRelative(String relativePath) {
        String clean = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        Path fullPath = baseDir.resolve(clean).normalize();
        if (!fullPath.startsWith(baseDir)) {
            throw new SecurityException("Chemin invalide");
        }
        return fullPath;
    }
}
