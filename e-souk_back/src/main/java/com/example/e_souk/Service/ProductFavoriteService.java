package com.example.e_souk.Service;

import com.example.e_souk.Dto.Product.ProductDTO;
import com.example.e_souk.Exception.ResourceNotFoundException;
import com.example.e_souk.Mappers.ProductMapper;
import com.example.e_souk.Model.Product;
import com.example.e_souk.Model.ProductFavorite;
import com.example.e_souk.Model.User;
import com.example.e_souk.Repository.ProductFavoriteRepository;
import com.example.e_souk.Repository.ProductRepository;
import com.example.e_souk.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des produits favoris
 * Contient la logique métier pour les opérations sur les favoris
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductFavoriteService {
    
    private final ProductFavoriteRepository productFavoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    /**
     * Récupère tous les favoris d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des DTOs des favoris
     */
    public List<ProductDTO> getUserFavorites(UUID userId) {
        log.debug("Récupération des favoris pour l'utilisateur: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        List<ProductFavorite> favorites = productFavoriteRepository.findByUserIdWithProductAndShop(userId);
        List<Product> productsFavorites = new ArrayList<>();
        for(ProductFavorite pv : favorites) {
            productsFavorites.add(pv.getProduct());
        }
        return productsFavorites.stream()
                .map(ProductMapper::toProductDetails)
			.collect(Collectors.toList());
    }
    
    /**
     * Ajoute un produit aux favoris
     * @param userId ID de l'utilisateur
     * @param productId ID du produit
     * @return DTO du favori créé
     */
    public ProductDTO addToFavorites(UUID userId, UUID productId) {
        log.info("Ajout aux favoris - Utilisateur: {}, Produit: {}", userId, productId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        // Vérifier si le produit est déjà en favori
        if (productFavoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalArgumentException("Le produit est déjà dans les favoris");
        }
        
        ProductFavorite favorite = ProductFavorite.builder()
                .user(user)
                .product(product)
                .build();
        
        ProductFavorite savedFavorite = productFavoriteRepository.save(favorite);
        return ProductMapper.toProductDetails(savedFavorite.getProduct());
    }
    
    /**
     * Supprime un produit des favoris
     * @param userId ID de l'utilisateur
     * @param productId ID du produit
     */
    public void removeFromFavorites(UUID userId, UUID productId) {
        log.info("Suppression des favoris - Utilisateur: {}, Produit: {}", userId, productId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        
        productFavoriteRepository.deleteByUserAndProduct(user, product);
    }
    
    /**
     * Vérifie si un produit est en favori pour un utilisateur
     * @param userId ID de l'utilisateur
     * @param productId ID du produit
     * @return true si le produit est en favori
     */
    public boolean isProductFavorite(UUID userId, UUID productId) {
        return productFavoriteRepository.existsByUserIdAndProductId(userId, productId);
    }
    

    
}
