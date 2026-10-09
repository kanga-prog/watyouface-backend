package com.watyouface.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ListingDTO {

    public Long id;
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 120, message = "Le titre ne peut pas dépasser 120 caractères")
    public String title;
    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    public String description;
    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    public Double price;
    public String image;
    public String status;
    public Long sellerId;
    public Long buyerId;

}
