package com.example.e_souk.Controller;

import com.example.e_souk.Dto.Commande.CommandeDTO;
import com.example.e_souk.Dto.User.UserProfileDTO;
import com.example.e_souk.Model.EtatCommande;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.CommandeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Contrôleur pour la gestion des commandes
 * Expose les endpoints REST pour les opérations sur les commandes
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class CommandeController {
    
    private final CommandeService commandeService;
    private final AuthService authService;


        /**
     * Crée une nouvelle commande à partir du panier
     * @param shopId ID de la boutique
     * @param deliveryAddress Adresse de livraison
     * @param deliveryPostalCode Code postal de livraison
     * @return Commande créée
     */
    @PostMapping("/add/{shopId}")
    public ResponseEntity<UUID> createOrderFromCart(
            @PathVariable String shopId) {
            UUID shopIdUuid = UUID.fromString(shopId);
            UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId(); 
         String deliveryAddress= profile.getAddress();
          String deliveryPostalCode=profile.getCodePostal();
        log.info("POST /api/orders/{}/create - Création de commande: shopId={}", userId, shopIdUuid);
        CommandeDTO order = commandeService.createOrderFromCart(userId, shopIdUuid, deliveryAddress, deliveryPostalCode);
        return ResponseEntity.ok(order.getId());
    } 


    
    /**
     * Met à jour l'état d'une commande
     * @param orderId ID de la commande
     * @param newEtat Nouvel état
     * @return Commande mise à jour
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<CommandeDTO> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam EtatCommande newEtat) {
        
        log.info("PUT /api/orders/{}/status - Mise à jour état: {}", orderId, newEtat);
        
        CommandeDTO order = commandeService.updateOrderStatus(orderId, newEtat);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Annule une commande
     * @param orderId ID de la commande
     * @return Commande annulée
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<CommandeDTO> cancelOrder(@PathVariable UUID orderId) {
        log.info("PUT /api/orders/{}/cancel - Annulation de la commande", orderId);
        
        CommandeDTO order = commandeService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }
}
