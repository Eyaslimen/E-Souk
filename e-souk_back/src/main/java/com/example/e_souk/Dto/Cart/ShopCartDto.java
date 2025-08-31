package com.example.e_souk.Dto.Cart;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant les produits d'une boutique dans le panier
 * Regroupe tous les articles d'une même boutique
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopCartDto {
    
    /**
     * ID de la boutique
     */
    private UUID shopId;
    
    /**
     * Nom de la boutique
     */
    private String shopName;
    
    /**
     * Liste des produits de cette boutique dans le panier
     */
    private List<CartItemDto> items;
    
    /**
     * Prix total des produits de cette boutique
     */
    private Float shopTotal;
    
    /**
     * Nombre d'articles différents de cette boutique
     */
    private Integer itemCount;
}