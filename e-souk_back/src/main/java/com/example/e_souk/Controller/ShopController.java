package com.example.e_souk.Controller;

import com.example.e_souk.Exception.ShopException;

import com.example.e_souk.Dto.CreateShopRequestDTO;
import com.example.e_souk.Dto.ShopDetailsDTO;
import com.example.e_souk.Dto.UpdateShopRequestDTO;
import com.example.e_souk.Dto.UserProfileDTO;
import com.example.e_souk.Model.User;
import com.example.e_souk.Dto.ShopResponseDTO;
import com.example.e_souk.Dto.ShopSummaryDTO;
import com.example.e_souk.Dto.ShopStatsDTO;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.ShopService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/**
 * Controller REST pour gérer les boutiques
 * 
 * ROUTES PRINCIPALES :
 * - POST /api/shops : Créer une boutique (CLIENT → VENDOR automatique)
 * - GET /api/shops : Lister toutes les boutiques
 * - GET /api/shops/{id} : Détails d'une boutique
 * - PUT /api/shops/{id} : Modifier sa boutique
 * - DELETE /api/shops/{id} : Désactiver sa boutique
 * - GET /api/shops/my-shop : Ma boutique
 * - GET /api/shops/search : Rechercher des boutiques
 */
@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;
    private final AuthService authService;
    
    /**
     * @param requestDTO données de la boutique
     * @return ResponseEntity avec la boutique créée
     */
    @PostMapping
    // SecurityContextHolder : il va vérifier dans le contexte de sécurité , s'il est authentifié l'utilisateur ou bien non 
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<ShopResponseDTO> createShop(
            @Valid @RequestBody CreateShopRequestDTO requestDTO) {
            User profile = authService.getCurrentUser();
        log.info("API - Création de boutique '{}' par l'utilisateur : {}", 
                requestDTO.getBrandName(), profile.getUsername());
        
        try {
            UUID ownerId = profile.getId();
            ShopResponseDTO createdShop = shopService.createShop(requestDTO, ownerId);
            
            log.info("API - Boutique créée avec succès - ID: {}, Owner: {}", 
                    createdShop.getId(), profile.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdShop);
            
        } catch (ShopException e) {
            log.warn("API - Erreur lors de la création de boutique : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * GET /api/shops - Récupérer toutes les boutiques actives
     * 
     * SÉCURITÉ : Accessible à tous (pas besoin d'être connecté)
     * USAGE : Page d'accueil, catalogue public
     * 
     * @param page numéro de la page
     * @param size taille de la page  
     * @param sort critère de tri
     * @return Page de boutiques (format résumé)
     */
    @GetMapping
    public ResponseEntity<Page<ShopSummaryDTO>> getAllActiveShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size, // 12 pour un affichage en grille 3x4
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        log.info("API - Récupération des boutiques - Page: {}, Size: {}, Sort: {}", page, size, sort);
        
        // PARSING du paramètre de tri
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        
        Page<ShopSummaryDTO> shops = shopService.getAllActiveShops(pageable);
        
        log.info("API - {} boutiques récupérées sur {} total", 
                shops.getNumberOfElements(), shops.getTotalElements());
        
        return ResponseEntity.ok(shops);
    }

    /**
     * GET /api/shops/{id} - Récupérer une boutique par son ID
     * 
     * SÉCURITÉ : Accessible à tous
     * USAGE : Page détail d'une boutique
     * 
     * @param id ID de la boutique
     * @return détails complets de la boutique
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShopDetailsDTO> getShopById(@PathVariable UUID id) {
        log.info("API - Récupération de la boutique ID: {}", id);
        
        try {
            ShopDetailsDTO shop = shopService.getShopById(id);
            return ResponseEntity.ok(shop);
            
        } catch (ShopException e) {
            log.warn("API - Boutique non trouvée : {}", e.getMessage());
            throw e;
        }
    }

    /**
     * GET /api/shops/my-shop - Récupérer MA boutique
     * 
     * SÉCURITÉ : Utilisateur connecté avec rôle VENDOR
     * USAGE : Dashboard du vendeur
     * 
     * @param principal utilisateur connecté
     * @return ma boutique avec toutes les infos
     */
    @GetMapping("/my-shop")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ShopResponseDTO> getMyShop() {
        User profile = authService.getCurrentUser();
        log.info("API - Récupération de la boutique de l'utilisateur : {}", profile.getUsername());
        try {
            UUID ownerId = profile.getId();
            ShopResponseDTO shop = shopService.getShopByOwnerId(ownerId);
            
            return ResponseEntity.ok(shop);
            
        } catch (ShopException e) {
            log.warn("API - Boutique non trouvée pour l'utilisateur : {}",  profile.getUsername());
            throw e;
        }
    }

    /**
     * PUT /api/shops/{id} - Mettre à jour une boutique
     * 
     * SÉCURITÉ : Seul le propriétaire peut modifier sa boutique
     * 
     * @param id ID de la boutique à modifier
     * @param requestDTO nouvelles données
     * @param principal utilisateur connecté
     * @return boutique mise à jour
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<ShopResponseDTO> updateShop(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateShopRequestDTO requestDTO) {
                User profile = authService.getCurrentUser();

        log.info("API - Mise à jour de la boutique ID: {} par : {}", id, profile.getUsername());

        try {
            UUID ownerId = profile.getId();
            ShopResponseDTO updatedShop = shopService.updateShop(id, requestDTO, ownerId);
            
            log.info("API - Boutique mise à jour avec succès");
            return ResponseEntity.ok(updatedShop);
            
        } catch (ShopException e) {
            log.warn("API - Erreur lors de la mise à jour : {}", e.getMessage());
            throw e;
        }
    }


    // =================== GESTION D'ERREURS ===================

    /**
     * Gestion des erreurs de validation et métier
     */
    @ExceptionHandler(ShopException.class)
    public ResponseEntity<ErrorResponse> handleShopException(ShopException e) {
        log.error("API - Erreur de validation : {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            e.getCode(),
            e.getMessage(),
            System.currentTimeMillis()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Gestion des erreurs d'accès non autorisé
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException e) {
        
        log.warn("API - Accès non autorisé : {}", e.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "ACCESS_DENIED",
            "Vous n'avez pas les permissions nécessaires pour effectuer cette action",
            System.currentTimeMillis()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Classe interne pour les réponses d'erreur standardisées
     */
    public static class ErrorResponse {
        private String code;
        private String message;
        private long timestamp;

        public ErrorResponse(String code, String message, long timestamp) {
            this.code = code;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getters pour la sérialisation JSON
        public String getCode() { return code; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}






// ######## LORSQUE ON AVANCE DANS LE PROJET ####



    // /**
    //  * DELETE /api/shops/{id} - Désactiver une boutique (soft delete)
    //  * 
    //  * SÉCURITÉ : Seul le propriétaire peut désactiver sa boutique
    //  * 
    //  * @param id ID de la boutique à désactiver
    //  * @param principal utilisateur connecté
    //  * @return réponse vide 204 No Content
    //  */
    // @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('VENDOR')")
    // public ResponseEntity<Void> deactivateShop(
    //         @PathVariable UUID id,
    //         Principal principal) {
        
    //     log.info("API - Désactivation de la boutique ID: {} par : {}", id, principal.getName());
        
    //     try {
    //         UUID ownerId = getUserIdFromPrincipal(principal);
    //         shopService.deactivateShop(id, ownerId);
            
    //         log.info("API - Boutique désactivée avec succès");
    //         return ResponseEntity.noContent().build();
            
    //     } catch (IllegalArgumentException e) {
    //         log.warn("API - Erreur lors de la désactivation : {}", e.getMessage());
    //         throw e;
    //     }
    // }

    // /**
    //  * GET /api/shops/search - Rechercher des boutiques
    //  * 
    //  * SÉCURITÉ : Accessible à tous
    //  * USAGE : Barre de recherche, auto-complétion
    //  * 
    //  * @param query terme de recherche
    //  * @return liste des boutiques correspondantes
    //  */
    // @GetMapping("/search")
    // public ResponseEntity<List<ShopSummaryDTO>> searchShops(
    //         @RequestParam(name = "q") String query) {
        
    //     log.info("API - Recherche de boutiques avec : '{}'", query);
        
    //     List<ShopSummaryDTO> shops = shopService.searchShops(query);
        
    //     log.info("API - {} boutiques trouvées", shops.size());
    //     return ResponseEntity.ok(shops);
    // }

    // /**
    //  * GET /api/shops/popular - Récupérer les boutiques populaires
    //  * 
    //  * SÉCURITÉ : Accessible à tous
    //  * USAGE : Section "Boutiques populaires" sur la page d'accueil
    //  * 
    //  * @param limit nombre max de boutiques à retourner
    //  * @return boutiques les plus suivies
    //  */
    // @GetMapping("/popular")
    // public ResponseEntity<Page<ShopSummaryDTO>> getPopularShops(
    //         @RequestParam(defaultValue = "6") int limit) {
        
    //     log.info("API - Récupération des {} boutiques les plus populaires", limit);
        
    //     Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "followerCount"));
    //     Page<ShopSummaryDTO> popularShops = shopService.getMostPopularShops(pageable);
        
    //     log.info("API - {} boutiques populaires récupérées", popularShops.getNumberOfElements());
    //     return ResponseEntity.ok(popularShops);
    // }

    // /**
    //  * GET /api/shops/{id}/stats - Récupérer les statistiques d'une boutique
    //  * 
    //  * SÉCURITÉ : Seul le propriétaire peut voir les stats de sa boutique
    //  * USAGE : Dashboard vendeur avec métriques détaillées
    //  * 
    //  * @param id ID de la boutique
    //  * @param principal utilisateur connecté
    //  * @return statistiques détaillées de la boutique
    //  */
    // @GetMapping("/{id}/stats")
    // @PreAuthorize("hasRole('VENDOR')")
    // public ResponseEntity<ShopStatsDTO> getShopStatistics(
    //         @PathVariable UUID id,
    //         Principal principal) {
        
    //     log.info("API - Récupération des statistiques de la boutique ID: {}", id);
        
    //     try {
    //         UUID ownerId = getUserIdFromPrincipal(principal);
    //         ShopStatsDTO stats = shopService.getShopStatistics(id, ownerId);
            
    //         return ResponseEntity.ok(stats);
            
    //     } catch (IllegalArgumentException e) {
    //         log.warn("API - Erreur lors de la récupération des stats : {}", e.getMessage());
    //         throw e;
    //     }
    // }


