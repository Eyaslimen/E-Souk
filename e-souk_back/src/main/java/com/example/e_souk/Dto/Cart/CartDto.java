package com.example.e_souk.Dto.Cart;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant le panier complet de l'utilisateur
 * Organisé par boutiques
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    
    private List<ShopCartDto> shopCarts;
    
    private Float totalPrice;

    private Integer totalItems;

    private Integer shopCount;
}