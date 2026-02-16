package com.watyouface;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
@SpringBootApplication(
    scanBasePackages = {
        "com.watyouface",
        "com.watyouface.config",
        "com.watyouface.controller",
        "com.watyouface.dto",
        "com.watyouface.entity",
        "com.watyouface.media",
        "com.watyouface.repository",
        "com.watyouface.security",
        "com.watyouface.service"
    }
)
public class WatYouFaceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WatYouFaceBackendApplication.class, args);
    }

    /**
     * Initialise le dossier média unique:
     * - {project_root}/media/avatars/
     * - copie default.png si absent
     *
     * URL publique attendue: /media/avatars/default.png
     * (servie via WebConfig: /media/** -> file:media/)
     */
    @PostConstruct
    public void initMedia() throws IOException {
        String base = System.getProperty("user.dir");

        Path avatarsDir = Paths.get(base, "media", "avatars").toAbsolutePath().normalize();
        if (!Files.exists(avatarsDir)) {
            Files.createDirectories(avatarsDir);
        }

        System.out.println("📁 Dossier media/avatars résolu : " + avatarsDir);
        System.out.println("📂 Existe ? " + Files.exists(avatarsDir));

        // Crée / copie le default.png si absent
        Path defaultAvatar = avatarsDir.resolve("default.png");
        if (!Files.exists(defaultAvatar)) {
            try {
                // Source côté resources (tu as déjà src/main/resources/static/default.png)
                Path packaged = Paths.get(base, "src", "main", "resources", "static", "default.png")
                    .toAbsolutePath().normalize();

                if (Files.exists(packaged)) {
                    Files.copy(packaged, defaultAvatar);
                    System.out.println("✅ default.png copié dans media/avatars");
                } else {
                    System.out.println("ℹ️ default.png introuvable dans resources/static — ajoute une image par défaut si tu veux.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de la copie du default avatar: " + e.getMessage());
            }
        }
    }
}
