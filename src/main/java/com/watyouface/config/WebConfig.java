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

        // Support EXISTANT (ne rien casser)
        Path uploadsDir = Paths.get(System.getProperty("user.dir"), "uploads")
                .toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsDir + "/")
                .setCachePeriod(3600);

        // Nouveau dossier propre
        Path mediaDir = Paths.get(System.getProperty("user.dir"), "media")
                .toAbsolutePath().normalize();
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:" + mediaDir + "/")
                .setCachePeriod(3600);

        // Pas obligatoire, mais sécurise redundancy
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
