package com.example.e_souk.Dto.Shop;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShopRequestDTO {
    @NotBlank(message = "Le nom de la marque est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom de la marque doit contenir entre 2 et 100 caractères")
    private String brandName;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    @Size(max = 50, message = "La categoryName ne peut pas dépasser 50 caractères")
    private String categoryName;

    @DecimalMin(value = "0.0", message = "Les frais de livraison ne peuvent pas être négatifs")
    @DecimalMax(value = "100.0", message = "Les frais de livraison ne peuvent pas dépasser 100€")
    private Float deliveryFee = 0.0f;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String address;
    private MultipartFile logoPicture;
}