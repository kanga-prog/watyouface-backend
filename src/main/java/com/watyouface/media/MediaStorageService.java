package com.watyouface.media;

import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class MediaStorageService {

    private static final String BASE_UPLOAD_DIR = "uploads/";

    public String getBaseUploadDir() {
        return BASE_UPLOAD_DIR;
    }

    /** Génère un chemin complet + crée le dossier parent */
    public String resolvePath(String relativePath) {
        String full = BASE_UPLOAD_DIR + relativePath;
        File file = new File(full);

        File parent = file.getParentFile();
        if (!parent.exists()) parent.mkdirs();

        return full;
    }
}
