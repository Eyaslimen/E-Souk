package com.example.e_souk.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopSummaryDTO {
    private UUID id;
    private String brandName;
    private String description;
    private String logoPicture;
    private String ownerUsername;
    private String ownerPicture;
    private Long productCount;
    private Long followerCount;
}
