package com.watyouface;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication(
    scanBasePackages = {
        "com.watyouface",           // package principal
        "com.watyouface.entity",    // package des entités JPA
        "com.watyouface.repository" // package des repositories
    }
)
public class WatYouFaceBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(WatYouFaceBackendApplication.class, args);
    }


    @PostConstruct
    public void logUploadPath() {
        String uploadDir = System.getProperty("user.dir") + "/uploads";
        System.out.println("📁 Dossier uploads résolu : " + uploadDir);
        System.out.println("📂 Existe ? " + new File(uploadDir).exists());
    }
}