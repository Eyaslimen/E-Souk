package com.example.e_souk.Controller;

import com.example.e_souk.Dto.Product.ProductDTO;
import com.example.e_souk.Dto.User.UserProfileDTO;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.ProductFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Contrôleur pour la gestion des produits favoris
 * Expose les endpoints REST pour les opérations sur les favoris
 */
@RestController
@RequestMapping("/api/products-favorites")
@RequiredArgsConstructor
@Slf4j
public class ProductFavoriteController {
    
    private final ProductFavoriteService productFavoriteService;
    private final AuthService authService;
    /**
     * Ajoute un produit aux favoris
     * @param userId ID de l'utilisateur
     * @param productId ID du produit
     * @return Favori créé
     */
    @PostMapping("/add/{productId}")
    public ResponseEntity<ProductDTO> addToFavorites(
            @PathVariable String productId) {
      UUID productIdUuid = UUID.fromString(productId);

        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId();
        log.info("POST /api/favorites/{}/products/{} - Ajout aux favoris", userId, productIdUuid);
        
        ProductDTO favorite = productFavoriteService.addToFavorites(userId, productIdUuid);
        return ResponseEntity.ok(favorite);
    }
    
    /**
     * Supprime un produit des favoris
     * @param productId ID du produit
     * @return Réponse de succès
     */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromFavorites(
            @PathVariable UUID productId) {
        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId();
        log.info("DELETE /api/favorites/{}/products/{} - Suppression des favoris", userId, productId);
        productFavoriteService.removeFromFavorites(userId, productId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Vérifie si un produit est en favori pour un utilisateur
     * @param userId ID de l'utilisateur
     * @param productId ID du produit
     * @return true si le produit est en favori
     */
    @GetMapping("/{productId}/check")
    public ResponseEntity<Boolean> isProductFavorite(
            @PathVariable UUID productId) {

        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId = profile.getId();
        log.info("GET /api/favorites/{}/products/{}/check - Vérification favori", userId, productId);
        
        boolean isFavorite = productFavoriteService.isProductFavorite(userId, productId);
        return ResponseEntity.ok(isFavorite);
    }
    

}
