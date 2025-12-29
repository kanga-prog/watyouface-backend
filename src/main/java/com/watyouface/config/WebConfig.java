package com.watyouface.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // === 1. Dossier UPLOADS général ===
        Path uploadsDir = Paths.get(System.getProperty("user.dir"), "uploads")
                .toAbsolutePath().normalize();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsDir + "/")
                .setCachePeriod(3600);


        // === 2. Dossier MEDIA général ===
        Path mediaDir = Paths.get(System.getProperty("user.dir"), "media")
                .toAbsolutePath().normalize();

        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:" + mediaDir + "/")
                .setCachePeriod(3600);


        // === 3. Dossier AVATARS (peu importe l’endroit) ===
        // Si tes avatars sont dans uploads/avatars/
        Path avatarsInUploads = uploadsDir.resolve("avatars");
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + avatarsInUploads + "/")
                .setCachePeriod(3600);

        // Si tes avatars sont dans media/avatars/
        Path avatarsInMedia = mediaDir.resolve("avatars");
        registry.addResourceHandler("/media/avatars/**")
                .addResourceLocations("file:" + avatarsInMedia + "/")
                .setCachePeriod(3600);


        // === 4. Fichiers statiques classiques ===
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
