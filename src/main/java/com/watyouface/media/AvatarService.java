package com.watyouface.media;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Service
public class AvatarService {

    private final Path avatarDir;

    public AvatarService() throws IOException {
        // Dossier absolu : {project_root}/media/avatars/
        this.avatarDir = Paths.get(System.getProperty("user.dir"), "media", "avatars")
                              .toAbsolutePath()
                              .normalize();

        Files.createDirectories(avatarDir);
    }

    public String saveAvatar(MultipartFile file, Long userId) throws IOException {

        if (file.isEmpty()) {
            throw new IOException("⚠ Le fichier uploadé est vide.");
        }

        // Récupérer l’extension réelle
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".")))
                .orElse(".jpg");

        // Nom de fichier propre
        String filename = "avatar_" + userId + "_" + System.currentTimeMillis() + extension;

        Path outputFile = avatarDir.resolve(filename);

        // Resize + conversion Thumbnailator (256x256)
        Thumbnails.of(file.getInputStream())
                .size(256, 256)
                .outputFormat(extension.replace(".", "")) // jpg / png
                .toFile(outputFile.toFile());

        // URL publique pour WebConfig (media/**)
        return "/media/avatars/" + filename;
    }
}
