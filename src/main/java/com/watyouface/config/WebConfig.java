package com.watyouface.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads")
                                 .toAbsolutePath().normalize();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String uploadPath = "file:" + uploadDir.toString() + "/";

            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(uploadPath) // emplacement absolu
                    .setCachePeriod(3600);

            log.info("WebConfig chargé — uploads: {}", uploadPath);
        } catch (IOException e) {
            log.error("Impossible de préparer le dossier uploads", e);
        }
    }
}
