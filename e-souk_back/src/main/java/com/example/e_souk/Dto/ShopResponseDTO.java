package com.example.e_souk.Dto;

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
    private String description;
    private String logoPicture;
    private Float deliveryFee;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ShopOwnerDTO owner;
    private Long productCount;
    private Long orderCount;
    private Long followerCount;
}
