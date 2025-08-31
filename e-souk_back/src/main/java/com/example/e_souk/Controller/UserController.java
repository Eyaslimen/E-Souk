package com.example.e_souk.Controller;

import com.example.e_souk.Dto.User.ChangePasswordDTO;
import com.example.e_souk.Dto.User.UserProfileDTO;
import com.example.e_souk.Model.User;
import com.example.e_souk.Service.UserService;
// import com.example.e_souk.Service.CartService;
import com.example.e_souk.Service.ProductFavoriteService;
// import com.example.e_souk.Service.CommandeService;
import com.example.e_souk.Service.ShopFollowerService;
import com.example.e_souk.Dto.Product.ProductDTO;
import com.example.e_souk.Dto.Shop.ShopSummaryDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Utilisateurs", description = "Endpoints pour la gestion des utilisateurs")
public class UserController {
    
    private final UserService userService;
    // private final CartService cartService;
    private final ProductFavoriteService productFavoriteService;
    // private final CommandeService commandeService;
    private final ShopFollowerService shopFollowerService;
    
    /**
     * Modifier un utilisateur
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    @Operation(
        summary = "Mise à jour du profil utilisateur",
        description = "Met à jour les informations du profil utilisateur"
    )
    public ResponseEntity<User> updateUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) {
        // Utiliser userService pour mettre à jour le profil utilisateur
        User updatedProfile = userService.updateUser(userProfileDTO);
        return ResponseEntity.ok(updatedProfile);
    }
     /**
     * Modifier le mot de passe 
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/updatePassword/{id}")
    @Operation(
        summary = "Mise à jour du mot de passe",
        description = "Met à jour le mot de passe de l'utilisateur"
    )
    public ResponseEntity<User> changePassword(@PathVariable("id") UUID id,@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        User updatedUser = userService.changePassword(id, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
        return ResponseEntity.ok(updatedUser);
    }
    /**
     * Récupère tous les utilisateurs
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    @Operation(
        summary = "recuperation de tous les utilisateurs",
        description = "Récupère tous les utilisateurs"
    )
    public ResponseEntity<List<UserProfileDTO>> getAll() {
        List<UserProfileDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    /**
     * Récupérer un utilisateur par son ID
     */
    
    /**
     * Supprimer un utilisateur
     */
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur non trouvé ou suppression échouée."));
        }
    }

    // ==================== NOUVELLES FONCTIONNALITÉS E-COMMERCE ====================

    /**
     * Récupère le panier d'un utilisateur
     */
    // @PreAuthorize("isAuthenticated()")
    // @GetMapping("/{userId}/cart")
    // @Operation(summary = "Récupérer le panier", description = "Récupère le panier d'un utilisateur")
    // public ResponseEntity<CartDTO> getUserCart(@PathVariable UUID userId) {
    //     CartDTO cart = cartService.getUserCart(userId);
    //     return ResponseEntity.ok(cart);
    // }

    /**
     * Récupère les favoris d'un utilisateur
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}/favorites")
    @Operation(summary = "Récupérer les favoris", description = "Récupère les produits favoris d'un utilisateur")
    public ResponseEntity<List<ProductDTO>> getUserFavorites(@PathVariable UUID userId) {
        List<ProductDTO> favorites = productFavoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }

    // /**
    //  * Récupère les commandes d'un utilisateur
    //  */
    // @PreAuthorize("isAuthenticated()")
    // @GetMapping("/{userId}/orders")
    // @Operation(summary = "Récupérer les commandes", description = "Récupère l'historique des commandes d'un utilisateur")
    // public ResponseEntity<List<CommandeDTO>> getUserOrders(@PathVariable UUID userId) {
    //     List<CommandeDTO> orders = commandeService.getUserOrders(userId);
    //     return ResponseEntity.ok(orders);
    // }

    /**
     * Récupère les boutiques suivies par un utilisateur
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}/followed-shops")
    @Operation(summary = "Récupérer les boutiques suivies", description = "Récupère les boutiques suivies par un utilisateur")
    public ResponseEntity<List<ShopSummaryDTO>> getUserFollowedShops(@PathVariable UUID userId) {
        List<ShopSummaryDTO> followedShops = shopFollowerService.getUserFollowedShops(userId);
        return ResponseEntity.ok(followedShops);
    }

    // /**
    //  * Récupère les statistiques d'un utilisateur
    //  */
    // @PreAuthorize("isAuthenticated()")
    // @GetMapping("/{userId}/stats")
    // @Operation(summary = "Récupérer les statistiques", description = "Récupère les statistiques e-commerce d'un utilisateur")
    // public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable UUID userId) {
    //     Long favoriteCount = productFavoriteService.getFavoriteCount(userId);
    //     Long followedShopCount = shopFollowerService.getUserFollowedShopCount(userId);
    //     CartDTO cart = cartService.getUserCart(userId);
        
    //     Map<String, Object> stats = Map.of(
    //         "favoriteCount", favoriteCount,
    //         "followedShopCount", followedShopCount,
    //         "cartItemCount", cart.getItemCount(),
    //         "cartTotalAmount", cart.getTotalAmount()
    //     );
        
    //     return ResponseEntity.ok(stats);
    // }
}