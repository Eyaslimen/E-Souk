package com.example.e_souk.Controller;

import com.example.e_souk.Controller.CartController.ErrorResponse;
import com.example.e_souk.Dto.Cart.CartDto;
import com.example.e_souk.Dto.Commande.UserOrdersDto;
import com.example.e_souk.Dto.Product.ProductDTO;
import com.example.e_souk.Dto.Shop.ShopSummaryDTO;
import com.example.e_souk.Dto.User.UserProfileDTO;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.CartService;
import com.example.e_souk.Service.CommandeService;
import com.example.e_souk.Service.ProductFavoriteService;
import com.example.e_souk.Service.ShopFollowerService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des followers des boutiques
 * Expose les endpoints REST pour les opérations sur les followers
 */
@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
@Slf4j
public class UserInformationsController {
     private final AuthService authService;
     private final ShopFollowerService shopFollowerService;
     private final ProductFavoriteService productFavoriteService;
     private final CartService cartService;
     private final CommandeService commandeService;
    
     /**
     * Récupération du profil de l'utilisateur authentifié
     * @return Profil utilisateur
     */
    @GetMapping("/details")
        // Lorsque tu utilises @PreAuthorize, Spring Security intercepte l’appel à la méthode avant son exécution, et :
        // Accède à l’utilisateur courant via le SecurityContextHolder
        // Vérifie la condition (isAuthenticated(), hasRole(...), etc.)
        // S’il n’a pas les droits => Exception AccessDeniedException.
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Récupération du profil utilisateur",
        description = "Récupère les informations du profil de l'utilisateur authentifié"
    )
    public ResponseEntity<UserProfileDTO> getProfile() {
        log.debug("Requête de récupération de profil");
        UserProfileDTO profile = authService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    /**
     * Récupère toutes les boutiques suivies par un utilisateur
     * @return Liste des boutiques suivies
     */
    @GetMapping("/shops")
    public ResponseEntity<List<ShopSummaryDTO>> getUserFollowedShops() {
        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId();
        log.info("GET /api/shop-followers/user/{} - Récupération des boutiques suivies", userId);
        
        List<ShopSummaryDTO> followedShops = shopFollowerService.getUserFollowedShops(userId);
        return ResponseEntity.ok(followedShops);
    }

    /**
     * Récupère tous les favoris d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des favoris
     */
    @GetMapping("/favoris")
    public ResponseEntity<List<ProductDTO>> getUserFavorites() {
        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId();
        log.info("GET /api/favorites/{} - Récupération des favoris", userId);
        List<ProductDTO> favorites = productFavoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    /**
     * Récupère le panier de l'utilisateur connecté
     * @param authentication Informations d'authentification
     * @return Panier de l'utilisateur
     */
    @GetMapping("/cart")
    public ResponseEntity<?> getCart() {
        try {
            UserProfileDTO profile = authService.getCurrentUserProfile();
            UUID userId = profile.getId();
            CartDto cart = cartService.getCartByUserId(userId);
            
            return ResponseEntity.ok(cart);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur serveur", e.getMessage()));
        }
    }
    /**
     * Récupère toutes les commandes d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des commandes
     */
    @GetMapping("/orders")
    public ResponseEntity<UserOrdersDto> getUserOrdersByShop() {
        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId();
        log.info("GET /api/orders/user/{} - Récupération des commandes", userId);

        UserOrdersDto userOrders = commandeService.getUserOrdersByShop(userId);
        return ResponseEntity.ok(userOrders);
    }
}