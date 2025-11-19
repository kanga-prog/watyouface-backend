package com.watyouface.media;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import java.io.IOException;

@Service
public class ImageService {

    private final MediaStorageService storage;

    public ImageService(MediaStorageService storage) {
        this.storage = storage;
    }

    /** Sauvegarde image d'un post (max 1080px de large) */
    public String savePostImage(MultipartFile file, Long postId) throws IOException {

        String relativePath = "posts/post_" + postId + ".jpg";
        String outputPath = storage.resolvePath(relativePath);

        Thumbnails.of(file.getInputStream())
                .size(1080, 0)
                .outputFormat("jpg")
                .toFile(outputPath);

        return "/" + relativePath;
    }
}
