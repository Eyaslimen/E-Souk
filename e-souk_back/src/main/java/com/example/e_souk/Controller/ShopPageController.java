package com.example.e_souk.Controller;

import com.example.e_souk.Exception.ShopException;
import com.example.e_souk.Dto.Product.ProductDetailsDTO;
import com.example.e_souk.Dto.Review.CreateReviewDTO;
import com.example.e_souk.Dto.Review.ReviewResponseDTO;
import com.example.e_souk.Dto.Shop.ShopGeneralDetailsDTO;
import com.example.e_souk.Model.User;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.ShopPageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
public class ShopPageController {

    private final ShopPageService shopPageService;
    private final AuthService authService;
 /**
     * GET /api/shops/{name} - Récupérer une boutique par son Name
     * 
     * SÉCURITÉ : Accessible à tous
     * USAGE : Page détail d'une boutique
     * 
     * @param name Name de la boutique
     * @return détails complets de la boutique
     */
    @GetMapping("/{name}")
    public ResponseEntity<ShopGeneralDetailsDTO> getShopByName(@PathVariable String name) {
        log.info("API - Récupération de la boutique ID: {}", name);
        try {
            log.info("teeeeeeeeeeeeeesst shop");
            ShopGeneralDetailsDTO shop = shopPageService.getShopByName(name);
            log.info("this is the shop",shop);
            log.info("geeeeeeeeeeeeeeeeeet it !!!");
            return ResponseEntity.ok(shop);
        }
        catch (ShopException e) {
            log.warn("API - Boutique non trouvée : {}", e.getMessage());
            throw e;
        }
    }
       /**
     * GET /api/{shopName}/products - Récupérer les produits d'une boutique
     * 
     * SÉCURITÉ : Accessible à tous
     * USAGE : Page détail d'une boutique
     * 
     * @param name Name de la boutique
     * @return tous les produits d'une boutique
     */
    @GetMapping("/{shopName}/products")
        public ResponseEntity<List<ProductDetailsDTO>> getShopProducts(@PathVariable String shopName) {
        List<ProductDetailsDTO> products = shopPageService.getProductsByShop(shopName);
        return ResponseEntity.ok(products);
    }
 
    /**
     * Post /api/shops/{name} - Ajouter review a un shop
     * 
     * SÉCURITÉ : Accessible à tous
     * USAGE : Page détail d'une boutique
     * @param name Name de la boutique
     * @return tous les produits d'une boutique
     */
    @PostMapping("/{shopName}/review")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDTO> createReview(@PathVariable String shopName, @Valid @RequestBody CreateReviewDTO requestDTO) {
                User profile = authService.getCurrentUser();
                UUID id = profile.getId();
            log.info("Creating review for shop: {}", shopName);
            log.info("pour le user: {}", id);
            log.info("avec le data: {}", requestDTO);

        ReviewResponseDTO createdReview = shopPageService.createReview(requestDTO,shopName,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }
    //get reviews by shop Name
    @GetMapping("/{shopName}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getShopReviews(@PathVariable String shopName) {
        List<ReviewResponseDTO> reviews = shopPageService.getReviewsByShopName(shopName);
        return ResponseEntity.ok(reviews);
    }

}