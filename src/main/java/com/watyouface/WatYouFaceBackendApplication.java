package com.watyouface;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(WatYouFaceBackendApplication.class);

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

        log.info("Media avatar directory initialized (exists={})", Files.exists(avatarsDir));

        // Crée / copie le default.png si absent
        Path defaultAvatar = avatarsDir.resolve("default.png");
        if (!Files.exists(defaultAvatar)) {
            try {
                // Source côté resources (tu as déjà src/main/resources/static/default.png)
                Path packaged = Paths.get(base, "src", "main", "resources", "static", "default.png")
                    .toAbsolutePath().normalize();

                if (Files.exists(packaged)) {
                    Files.copy(packaged, defaultAvatar);
                    log.info("Default avatar initialized");
                } else {
                    log.warn("Default avatar resource is not available");
                }
            } catch (Exception e) {
                log.error("Default avatar initialization failed ({})", e.getClass().getSimpleName());
            }
        }
    }
}
