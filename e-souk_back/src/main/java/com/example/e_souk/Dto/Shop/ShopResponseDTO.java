package com.example.e_souk.Dto.Shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponseDTO {
    private UUID id;
    private String brandName;
    private String bio;
    private String description;
    private String logoPicture;
    private Float deliveryFee;
    private String address;
    private String categoryName;

    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String instagramLink;
    private String facebookLink;
    private String phone;

    private ShopOwnerDTO owner;
    private Long productCount;
    private Long orderCount;
    private Long followerCount;
}
