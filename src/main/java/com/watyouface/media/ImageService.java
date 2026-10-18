package com.watyouface.media;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageService {

    private final ImageUploadService imageUploadService;

    @Autowired
    public ImageService(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    /** Compatibility constructor for focused tests and legacy wiring. */
    public ImageService(MediaStorageService storage) {
        this(new ImageUploadService(storage));
    }

    /** Sauvegarde image d'un post (max 1080px de large) */
    public String savePostImage(MultipartFile file, Long postId) throws IOException {

        return imageUploadService.store(file, "posts", "post");
    }
}
