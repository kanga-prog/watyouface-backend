package com.watyouface.media;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MarketplaceImageService {

    private final ImageUploadService imageUploadService;

    @Autowired
    public MarketplaceImageService(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    /** Compatibility constructor for focused tests and legacy wiring. */
    public MarketplaceImageService(MediaStorageService storage) {
        this(new ImageUploadService(storage));
    }

    /**
     * Sauvegarde une image d’annonce marketplace dans media/marketplace/
     * et retourne l’URL publique /media/marketplace/<file>.jpg
     */
    public String saveListingImage(MultipartFile file) throws IOException {
        return imageUploadService.store(file, "marketplace", "listing");
    }
}
