package com.example.e_souk.Dto.Shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.e_souk.Dto.Product.ProductDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailsDTO {
    private UUID id;
    private String brandName;
    private String description;
    private String logoPicture;
    private Float deliveryFee;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String ownerName;
    private String ownerPicture;
    private List<ProductDTO> products;
    private Long orderCount;
    private Long followerCount;
}
