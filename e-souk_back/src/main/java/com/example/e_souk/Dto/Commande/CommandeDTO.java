package com.example.e_souk.Dto.Commande;

import com.example.e_souk.Model.EtatCommande;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO pour représenter une commande
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeDTO {
    
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private String customerName;
    private UUID shopId;
    private String shopName;
    private String deliveryAddress;
    private String deliveryPostalCode;
    private Float total;
    private Float deliveryFee;
    private Float subtotal;
    private EtatCommande etat;
    private Integer totalItemCount;
    private Long uniqueItemCount;
    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private List<OrderItemDTO> orderItems;
}
