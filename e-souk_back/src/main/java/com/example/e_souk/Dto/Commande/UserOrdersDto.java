package com.example.e_souk.Dto.Commande;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserOrdersDto {
    private List<ShopOrdersDto> shopOrders;
    private Integer totalOrders;
    private Integer shopCount;
}