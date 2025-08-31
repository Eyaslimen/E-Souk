package com.example.e_souk.Controller;

import com.example.e_souk.Dto.Cart.AddToCartRequest;
import com.example.e_souk.Dto.Cart.CartDto;
import com.example.e_souk.Dto.Cart.CartItemDto;
import com.example.e_souk.Dto.User.UserProfileDTO;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Contrôleur pour la gestion des paniers
 * Expose les endpoints REST pour les opérations sur les paniers
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    
    private final CartService cartService;
    private final AuthService authService;

    /**
     * Ajoute un article au panier de l'utilisateur connecté
     * @param request Données de l'article à ajouter
     * @param authentication Informations d'authentification
     * @return Article ajouté au panier
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @Valid @RequestBody AddToCartRequest request) {
        try {
            // Récupération de l'ID utilisateur depuis l'authentification
            UserProfileDTO profile = authService.getCurrentUserProfile();
            UUID userId = profile.getId();

            // Ajout de l'article au panier
            CartItemDto cartItem = cartService.addToCart(userId, request);
            return ResponseEntity.ok(cartItem.getId());
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erreur de validation", e.getMessage()));
                    
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur serveur", e.getMessage()));
        }
    }

    /**
     * Met à jour la quantité d'un article dans le panier
     * @param cartItemId ID de l'article du panier
     * @param quantity Nouvelle quantité
     * @param authentication Informations d'authentification
     * @return Article mis à jour
     */
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable UUID cartItemId,
            @RequestParam Integer quantity
           ) {
        
        try {
            UserProfileDTO profile = authService.getCurrentUserProfile();
            UUID userId = profile.getId();
            CartItemDto updatedItem = cartService.updateCartItemQuantity(userId, cartItemId, quantity);

            return ResponseEntity.ok(updatedItem); 
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erreur de validation", e.getMessage()));
                    
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur serveur", e.getMessage()));
        }
    }

    /**
     * Supprime un article du panier
     * @param cartItemId ID de l'article à supprimer
     * @param authentication Informations d'authentification
     * @return Confirmation de suppression
     */
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable UUID cartItemId) {
        
        try {
            UserProfileDTO profile = authService.getCurrentUserProfile();
            UUID userId = profile.getId();
            cartService.removeFromCart(userId, cartItemId);
            return ResponseEntity.ok(new SuccessResponse("Article supprimé du panier avec succès"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Erreur de validation", e.getMessage()));
                    
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur serveur", e.getMessage()));
        }
    }

    /**
     * Vide complètement le panier de l'utilisateur
     * @param authentication Informations d'authentification
     * @return Confirmation de vidage
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        
        try {
            UserProfileDTO profile = authService.getCurrentUserProfile();
            UUID userId = profile.getId();
            cartService.clearCart(userId);
            
            return ResponseEntity.ok(new SuccessResponse("Panier vidé avec succès"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur serveur", e.getMessage()));
        }
    }




    /**
     * Classe pour les réponses d'erreur
     */
    public static class ErrorResponse {
        private String error;
        private String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
        
        // Getters
        public String getError() { return error; }
        public String getMessage() { return message; }
    }

    /**
     * Classe pour les réponses de succès
     */
    public static class SuccessResponse {
        private String message;
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        // Getter
        public String getMessage() { return message; }
    }
}