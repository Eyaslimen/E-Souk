package com.example.e_souk.Dto.Product;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ProductDTO {
    private UUID id;
    private String name;
    private String description;
    private Float price;
    private String picture;
    private String categoryName;
    private String shopName;
}
