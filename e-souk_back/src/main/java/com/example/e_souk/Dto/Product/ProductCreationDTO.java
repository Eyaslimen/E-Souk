// package com.example.e_souk.Dto.Product;

// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;
// import lombok.Builder;

// import java.util.List;
// import java.util.UUID;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class ProductCreationDTO {
//     private String name;
//     private String description;
//     private String picture;
//     private UUID categoryId;
//     private UUID shopId;
//     private List<VariantDTO> variants;

//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     public static class VariantDTO {
//         private Float price;
//         private Integer stock;
//         private List<AttributeValueDTO> attributes;
//     }

//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     @Builder
//     public static class AttributeValueDTO {
//         private UUID attributeId;
//         private String value;
//     }
// }
