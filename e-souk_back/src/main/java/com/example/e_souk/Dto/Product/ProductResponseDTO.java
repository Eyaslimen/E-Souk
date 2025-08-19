package com.example.e_souk.Dto.Product;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ProductResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private String picture;
    private boolean isActive;
    private CategorySummaryDTO category;
    private ShopSummaryDTO shop;
    private List<VariantSummaryDTO> variants;

    @Data
    public static class CategorySummaryDTO {
        private UUID id;
        private String name;
    }

    @Data
    public static class ShopSummaryDTO {
        private UUID id;
        private String brandName;
    }

    @Data
    public static class VariantSummaryDTO {
        private UUID id;
        private String sku;
        private float price;
        private int stock;
        private List<AttributeValueDTO> attributeValues;
    }

    @Data
    public static class AttributeValueDTO {
        private String attributeName;
        private String value;
    }
}
