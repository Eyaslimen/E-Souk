package com.example.e_souk.Repository;

import com.example.e_souk.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour gérer les opérations CRUD sur les produits
 * 
 * POURQUOI ce repository est complexe ?
 * - Les produits ont des relations avec Shop, Category, Variant
 * - Besoins de recherche avancée (nom, catégorie, boutique)
 * - Gestion des stocks via les variantes
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    /**
     * Trouve tous les produits d'une boutique
     * 
     * USAGE : Dashboard vendeur, catalogue d'une boutique
     * 
     * @param shopId ID de la boutique
     * @param pageable pagination
     * @return Page<Product> produits de cette boutique
     */
    @Query("SELECT p FROM Product p WHERE p.shop.id = :shopId ORDER BY p.createdAt DESC")
    Page<Product> findByShopId(@Param("shopId") UUID shopId, Pageable pageable);
    
    /**
     * Trouve tous les produits d'une catégorie
     * 
     * USAGE : Navigation par catégorie côté client
     * 
     * @param categoryId ID de la catégorie
     * @param pageable pagination
     * @return Page<Product> produits de cette catégorie
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.createdAt DESC")
    Page<Product> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
    
    /**
     * Recherche de produits par nom (insensible à la casse)
     * 
     * USAGE : Barre de recherche, auto-complétion
     * 
     * @param searchTerm terme de recherche
     * @param pageable pagination
     * @return Page<Product> produits correspondants
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Recherche avancée : nom + catégorie + boutique
     * 
     * USAGE : Recherche avec filtres
     * 
     * @param searchTerm terme de recherche (peut être null)
     * @param categoryId ID catégorie (peut être null)
     * @param shopId ID boutique (peut être null)
     * @param pageable pagination
     * @return Page<Product> produits correspondants
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:shopId IS NULL OR p.shop.id = :shopId)")
    Page<Product> findWithFilters(@Param("searchTerm") String searchTerm,
                                 @Param("categoryId") UUID categoryId,
                                 @Param("shopId") UUID shopId,
                                 Pageable pageable);
    
    /**
     * Trouve les produits avec stock disponible
     * 
     * LOGIQUE COMPLEXE : Un produit a du stock si au moins une de ses variantes a stock > 0
     * 
     * @param pageable pagination
     * @return Page<Product> produits en stock
     */
    @Query("SELECT DISTINCT p FROM Product p " +
           "JOIN p.variants v " +
           "WHERE v.stock > 0")
    Page<Product> findProductsInStock(Pageable pageable);
    
    /**
     * Trouve les produits en rupture de stock
     * 
     * LOGIQUE : Toutes les variantes ont stock = 0
     * 
     * @param shopId ID de la boutique
     * @return List<Product> produits en rupture
     */
    @Query("SELECT p FROM Product p WHERE p.shop.id = :shopId AND " +
           "NOT EXISTS (SELECT v FROM Variant v WHERE v.product.id = p.id AND v.stock > 0)")
    List<Product> findOutOfStockProductsByShop(@Param("shopId") UUID shopId);
    
    /**
     * Compte le nombre total de variantes d'un produit
     * 
     * @param productId ID du produit
     * @return long nombre de variantes
     */
    @Query("SELECT COUNT(v) FROM Variant v WHERE v.product.id = :productId")
    long countVariantsByProductId(@Param("productId") UUID productId);
    
    /**
     * Calcule le stock total d'un produit (somme de toutes ses variantes)
     * 
     * @param productId ID du produit
     * @return long stock total
     */
    @Query("SELECT COALESCE(SUM(v.stock), 0) FROM Variant v WHERE v.product.id = :productId")
    long getTotalStockByProductId(@Param("productId") UUID productId);
    
    /**
     * Trouve le prix minimum d'un produit
     * 
     * USAGE : Affichage "À partir de X€"
     * 
     * @param productId ID du produit
     * @return Optional<Float> prix minimum
     */
    @Query("SELECT MIN(v.price) FROM Variant v WHERE v.product.id = :productId")
    Optional<Float> getMinPriceByProductId(@Param("productId") UUID productId);
    
    /**
     * Trouve le prix maximum d'un produit
     * 
     * @param productId ID du produit
     * @return Optional<Float> prix maximum
     */
    @Query("SELECT MAX(v.price) FROM Variant v WHERE v.product.id = :productId")
    Optional<Float> getMaxPriceByProductId(@Param("productId") UUID productId);
    
    /**
     * Vérifie si un produit appartient à une boutique
     * 
     * SÉCURITÉ : Pour vérifier que le vendeur peut modifier son produit
     * 
     * @param productId ID du produit
     * @param shopId ID de la boutique
     * @return boolean true si le produit appartient à cette boutique
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.id = :productId AND p.shop.id = :shopId")
    boolean existsByIdAndShopId(@Param("productId") UUID productId, @Param("shopId") UUID shopId);
    
    /**
     * Trouve les produits les plus récents d'une boutique
     * 
     * USAGE : Section "Nouveautés" sur la page boutique
     * 
     * @param shopId ID de la boutique
     * @param limit nombre de produits à retourner
     * @return List<Product> produits récents
     */
    @Query("SELECT p FROM Product p WHERE p.shop.id = :shopId ORDER BY p.createdAt DESC")
    List<Product> findRecentProductsByShop(@Param("shopId") UUID shopId, Pageable pageable);
    
    /**
     * Génère un SKU unique basé sur le nom du produit
     * 
     * USAGE : Auto-génération des SKU
     * Cette méthode compte les produits existants avec un nom similaire
     * 
     * @param productNamePrefix préfixe basé sur le nom du produit
     * @return long nombre de produits avec ce préfixe
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.name LIKE :productNamePrefix%")
    long countProductsWithNamePrefix(@Param("productNamePrefix") String productNamePrefix);
}