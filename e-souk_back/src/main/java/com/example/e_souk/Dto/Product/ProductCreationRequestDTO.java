package com.example.e_souk.Dto.Product;

import lombok.Data;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class ProductCreationRequestDTO {
    private String name;
    private String description;
    private String categoryName;
    private MultipartFile imageUrl;
    private float price;
    
    // Champs pour recevoir les données JSON comme strings
    private String attributesJson;
    private String variantsJson;
    
    // Champs transients pour les objets désérialisés
    @JsonIgnore
    private List<AttributeDTO> attributes;
    @JsonIgnore
    private List<VariantDTO> variants;

    @Data
    public static class AttributeDTO {
        private String name;
        private List<String> values;
    }

    @Data
    public static class VariantDTO {
        private List<AttributeValueDTO> attributeValues;
        private int stock;
    }

    @Data
    public static class AttributeValueDTO {
        private String attributeName;
        private String value;
    }
}