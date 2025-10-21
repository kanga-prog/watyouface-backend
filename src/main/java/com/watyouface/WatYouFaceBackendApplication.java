package com.watyouface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
}
