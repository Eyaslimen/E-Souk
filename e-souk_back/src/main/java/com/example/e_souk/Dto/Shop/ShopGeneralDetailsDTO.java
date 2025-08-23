package com.example.e_souk.Dto.Shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopGeneralDetailsDTO {
    private String brandName;
     private String bio;
    private String description;
    private String logoPicture;
    private Float deliveryFee;
    private String address;
    private LocalDateTime createdAt;
    private String ownerName;
    private String ownerPicture;
    private Long productCount;
    private Long followerCount;
    private String categoryName;
    private String phone;
        private String instagramLink;
    private String facebookLink;

}