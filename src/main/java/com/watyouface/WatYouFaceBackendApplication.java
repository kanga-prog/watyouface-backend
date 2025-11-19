package com.watyouface;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @PostConstruct
    public void initUploads() throws IOException {
        String base = System.getProperty("user.dir");
        Path uploads = Paths.get(base, "uploads", "avatars");
        if (!Files.exists(uploads)) {
            Files.createDirectories(uploads);
        }
        System.out.println("📁 Dossier uploads résolu : " + uploads.toAbsolutePath());
        System.out.println("📂 Existe ? " + Files.exists(uploads));

        // Optionnel : créer un default.png si absent (tu peux copier un fichier depuis resources)
        Path defaultAvatar = uploads.resolve("default.png");
        if (!Files.exists(defaultAvatar)) {
            // essaie de copier un fichier packaged dans resources/static/default-avatar.png
            try {
                Path packaged = Paths.get(base, "src", "main", "resources", "static", "default.png");
                if (Files.exists(packaged)) {
                    Files.copy(packaged, defaultAvatar);
                    System.out.println("✅ default.png copié dans uploads/avatars");
                } else {
                    System.out.println("ℹ️ default.png introuvable dans resources — ajoute une image par défaut si tu veux.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de la copie du default avatar: " + e.getMessage());
            }
        }
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        String base = System.getProperty("user.dir");
        String uploadsPath = "file:" + Paths.get(base, "uploads").toAbsolutePath().toString() + "/";
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // expose /uploads/** -> <project-root>/uploads/
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations(uploadsPath);
            }
        };
    }
}
