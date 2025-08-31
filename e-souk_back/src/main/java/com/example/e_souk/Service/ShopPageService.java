package com.example.e_souk.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_souk.Dto.Product.ProductDTO;
import com.example.e_souk.Dto.Review.CreateReviewDTO;
import com.example.e_souk.Dto.Review.ReviewResponseDTO;
import com.example.e_souk.Dto.Shop.ShopGeneralDetailsDTO;
import com.example.e_souk.Exception.ShopException;
import com.example.e_souk.Mappers.ProductMapper;
import com.example.e_souk.Mappers.ReviewMapper;
import com.example.e_souk.Mappers.ShopMapper;
import com.example.e_souk.Model.Review;
import com.example.e_souk.Model.Shop;
import com.example.e_souk.Model.User;
import com.example.e_souk.Repository.ProductRepository;
import com.example.e_souk.Repository.ReviewRepository;
import com.example.e_souk.Repository.ShopRepository;
import com.example.e_souk.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service pour gérer la logique métier des boutiques
 * 
 * RESPONSABILITÉS PRINCIPALES :
 * 1. Créer une boutique ET changer le rôle CLIENT → VENDOR
 * 2. Gérer les boutiques (CRUD)
 * 3. Calculer les statistiques
 * 4. Appliquer les règles métier
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopPageService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository; 

 /**
     * Crée un avis pour une boutique
     * 
     * @param createReviewDTO données de l'avis
     * @param shopName nom de la boutique
     * @param userId ID de l'utilisateur (récupéré du JWT)
     * @return ReviewResponseDTO l'avis créé
     */
    @Transactional
    public ReviewResponseDTO createReview(CreateReviewDTO createReviewDTO, String shopName, UUID userId) {
        log.info("Création d'un avis pour la boutique '{}' par l'utilisateur ID: {}", shopName, userId);
        // Récupération de la boutique
        Optional<Shop> shop = shopRepository.findByBrandNameIgnoreCase(shopName);
        Shop theShop = shop.orElseThrow(() -> {
            log.error("Boutique non trouvée avec le nom: {}", shopName);
            return new ShopException("SHOP_ERROR", "Boutique non trouvée");
        }); 
        // Récupération de l'utilisateur
            User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé avec l'ID: {}", userId);
                    return new ShopException("SHOP_ERROR", "Utilisateur non trouvé");
                });


        Review review = new Review();
        review.setComment(createReviewDTO.getContent());
        review.setRating(createReviewDTO.getRating());
        review.setType(createReviewDTO.getReviewType());
        review.setShop(theShop);
        review.setUser(author);
        review.setCreatedAt(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        log.info("Avis créé avec succès - ID: {}", savedReview.getId());
        // Retourner le DTO de réponse
        return ReviewMapper.toResponseDTO(savedReview);
    }
    
    public ShopGeneralDetailsDTO getShopByName(String name) {
            log.info("Récupération de la boutique Name: {}", name);
        
    Shop shop = shopRepository.findByBrandNameIgnoreCase(name)
        .orElseThrow(() -> new ShopException("SHOP_ERROR", "Boutique non trouvée"));
    long productCount = shopRepository.countProductsInShop(shop.getId());
    long orderCount = shopRepository.countOrdersInShop(shop.getId());
    long followerCount = 0;
    return ShopMapper.toShopDetailsDTO(shop, productCount, followerCount);
        // TODO Auto-generated method stub
    }
    //Récupere les reviews 
    public List<ReviewResponseDTO> getReviewsByShopName(String shopName) {
        List<Review> reviews = reviewRepository.findByShop_BrandNameIgnoreCase(shopName);
        List<ReviewResponseDTO> reviewDTOs = reviews.stream()
            .map(ReviewMapper::toResponseDTO)
            .collect(Collectors.toList());
        return reviewDTOs;
    }
    //Récupère les produits d'un shop 
		public List<ProductDTO> getProductsByShop(String shopName) {
		// toProductDetails
		return productRepository.findProductsByshop(shopName).stream()
			.map(ProductMapper::toProductDetails)
			.collect(Collectors.toList());
	}

}