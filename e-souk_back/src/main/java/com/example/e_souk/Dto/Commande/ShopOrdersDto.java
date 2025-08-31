package com.example.e_souk.Dto.Commande;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ShopOrdersDto {
    private UUID shopId;
    private String shopName;
    private List<CommandeDTO> orders;
    private Integer orderCount;
}