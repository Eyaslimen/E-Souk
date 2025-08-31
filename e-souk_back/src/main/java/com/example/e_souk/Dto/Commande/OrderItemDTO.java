package com.example.e_souk.Dto.Commande;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO pour représenter un article de commande
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    
    private UUID id;
    private UUID commandeId;
    private UUID variantId;
    private UUID productId;
    private String productName;
    private String productImage;
    private String variantName;
    private Float price;
    private Integer quantity;
    private Float subTotal;
    private String shopName;
    private UUID shopId;
}
