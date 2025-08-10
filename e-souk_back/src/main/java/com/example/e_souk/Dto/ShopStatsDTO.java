package com.example.e_souk.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopStatsDTO {
    private UUID shopId;
    private String brandName;
    private Long totalProducts;
    private Long activeProducts;
    private Long outOfStockProducts;
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Double totalRevenue;
    private Double monthlyRevenue;
    private Long totalFollowers;
    private Long totalReviews;
    private Double averageRating;
}
