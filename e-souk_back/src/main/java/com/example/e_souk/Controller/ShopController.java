package com.example.e_souk.Controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_souk.Dto.Product.ProductDetailsDTO;
import com.example.e_souk.Dto.Product.ProductFilterDTO;
import com.example.e_souk.Dto.Shop.CreateShopRequestDTO;
import com.example.e_souk.Dto.Shop.ShopFilterDto;
import com.example.e_souk.Dto.Shop.ShopResponseDTO;
import com.example.e_souk.Dto.Shop.ShopSummaryDTO;
import com.example.e_souk.Dto.Shop.UpdateShopRequestDTO;
import com.example.e_souk.Exception.ShopException;
import com.example.e_souk.Model.User;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.ProductService;
import com.example.e_souk.Service.ShopService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final ProductService productService;
    /**
     * @param requestDTO données de la boutique
     * @return ResponseEntity avec la boutique créée
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // SecurityContextHolder : il va vérifier dans le contexte de sécurité , s'il est authentifié l'utilisateur ou bien non 
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<ShopResponseDTO> createShop(
            @ModelAttribute CreateShopRequestDTO requestDTO) {
            User profile = authService.getCurrentUser();
            log.info("teeeest category name"); 
        log.info(requestDTO.getCategoryName());
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
        } catch (java.io.IOException e) {
            log.error("API - Erreur IO lors de la création de boutique : {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(
                "IO_ERROR",
                "Erreur lors du traitement du fichier ou de la requête.",
                System.currentTimeMillis()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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

    // /**
    //  * GET /api/shops/{id} - Récupérer une boutique par son ID
    //  * 
    //  * SÉCURITÉ : Accessible à tous
    //  * USAGE : Page détail d'une boutique
    //  * 
    //  * @param id ID de la boutique
    //  * @return détails complets de la boutique
    //  */
    // @GetMapping("/{id}")
    // public ResponseEntity<ShopDetailsDTO> getShopById(@PathVariable UUID id) {
    //     log.info("API - Récupération de la boutique ID: {}", id);
        
    //     try {
    //         ShopDetailsDTO shop = shopService.getShopById(id);
    //         return ResponseEntity.ok(shop);
            
    //     } catch (ShopException e) {
    //         log.warn("API - Boutique non trouvée : {}", e.getMessage());
    //         throw e;
    //     }
    // }

   

    // @PreAuthorize("isAuthenticated()")
    // public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
    //     CategoryResponseDTO createdCategory = categoryService.createCategory(requestDTO);
    //     return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    // }


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

    @GetMapping("/all")
    public ResponseEntity<Page<ShopSummaryDTO>> getShops(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {

        ShopFilterDto filters = new ShopFilterDto();
        filters.setCategoryName(categoryName);
        filters.setAddress(address);
        filters.setSearchKeyword(searchKeyword);
        filters.setSortBy(sortBy);
        filters.setPage(page);
        filters.setPageSize(pageSize);

        Page<ShopSummaryDTO> shops = shopService.findShops(filters);

        return ResponseEntity.ok(shops);
    }
}
