package com.example.e_souk.Controller;

import com.example.e_souk.Dto.Shop.ShopSummaryDTO;
import com.example.e_souk.Dto.User.UserProfileDTO;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.ShopFollowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Contrôleur pour la gestion des followers des boutiques
 * Expose les endpoints REST pour les opérations sur les followers
 */
@RestController
@RequestMapping("/api/shop-followers")
@RequiredArgsConstructor
@Slf4j
public class ShopFollowerController {
    
    private final ShopFollowerService shopFollowerService;
    private final AuthService authService;
        /**
     * Suit une boutique
     * @param userId ID de l'utilisateur
     * @param shopId ID de la boutique
     * @return Follower créé
     */
    @PostMapping("/follow/{shopId}")
    public ResponseEntity<ShopSummaryDTO> followShop(
            @PathVariable UUID shopId) {
        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId();
        log.info("POST /api/shop-followers/{}/shops/{} - Suivre une boutique", userId, shopId);
        
        ShopSummaryDTO follower = shopFollowerService.followShop(userId, shopId);
        return ResponseEntity.ok(follower);
    }
     /**
     * Ne suit plus une boutique
     * @param shopId ID de la boutique
     * @return Réponse de succès
     */
    @DeleteMapping("/unfollow/{shopId}")
    public ResponseEntity<Void> unfollowShop(
            @PathVariable UUID shopId) {
        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId();
        log.info("DELETE /api/shop-followers/{}/shops/{} - Ne plus suivre une boutique", userId, shopId);
        
        shopFollowerService.unfollowShop(userId, shopId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Vérifie si un utilisateur suit une boutique
     * @param shopId ID de la boutique
     * @return true si l'utilisateur suit la boutique
     */
    @GetMapping("{shopId}/check")
    public ResponseEntity<Boolean> isFollowingShop(
            @PathVariable UUID shopId) {
        UserProfileDTO profile = authService.getCurrentUserProfile();
        UUID userId=profile.getId();
        log.info("GET /api/shop-followers/{}/shops/{}/check - Vérification follow", userId, shopId);
        
        boolean isFollowing = shopFollowerService.isFollowingShop(userId, shopId);
        return ResponseEntity.ok(isFollowing);
    }

}
