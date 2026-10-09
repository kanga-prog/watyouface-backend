package com.watyouface.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VideoTitleUpdateRequest {
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
