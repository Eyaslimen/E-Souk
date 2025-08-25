package com.example.e_souk.Repository;

import com.example.e_souk.Model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour gérer les variantes de produits
 * 
 * RÔLE CRUCIAL : Les variantes contiennent les prix et stocks réels
 * C'est ici que se joue la logique e-commerce !
 */
@Repository
public interface VariantRepository extends JpaRepository<Variant, UUID> {
    
    /**
     * Trouve toutes les variantes d'un produit
     * 
     * USAGE : Affichage des options d'un produit (tailles, couleurs...)
     * 
     * @param productId ID du produit
     * @return List<Variant> toutes les variantes du produit
     */
    @Query("SELECT v FROM Variant v WHERE v.product.id = :productId ORDER BY v.createdAt")
    List<Variant> findByProductId(@Param("productId") UUID productId);
    
    /**
     * Trouve une variante par son SKU unique
     * 
     * USAGE : Recherche rapide, gestion des commandes
     * 
     * @param sku SKU de la variante
     * @return Optional<Variant> la variante correspondante
     */
    Optional<Variant> findBySku(String sku);
    
    /**
     * Trouve les variantes en stock d'un produit
     * 
     * USAGE : Afficher seulement les options disponibles
     * 
     * @param productId ID du produit
     * @return List<Variant> variantes avec stock > 0
     */
    @Query("SELECT v FROM Variant v WHERE v.product.id = :productId AND v.stock > 0")
    List<Variant> findInStockVariantsByProductId(@Param("productId") UUID productId);
    
    /**
     * Trouve les variantes en rupture de stock d'un produit
     * 
     * USAGE : Alertes pour le vendeur
     * 
     * @param productId ID du produit
     * @return List<Variant> variantes avec stock = 0
     */
    @Query("SELECT v FROM Variant v WHERE v.product.id = :productId AND v.stock = 0")
    List<Variant> findOutOfStockVariantsByProductId(@Param("productId") UUID productId);
    
    /**
     * Trouve les variantes avec stock faible d'une boutique
     * 
     * USAGE : Alertes de réapprovisionnement
     * 
     * @param shopId ID de la boutique
     * @param threshold seuil de stock faible (ex: 5)
     * @return List<Variant> variantes avec stock <= threshold
     */
    @Query("SELECT v FROM Variant v WHERE v.product.shop.id = :shopId AND v.stock > 0 AND v.stock <= :threshold")
    List<Variant> findLowStockVariantsByShop(@Param("shopId") UUID shopId, @Param("threshold") int threshold);
    
    /**
     * Trouve la variante la moins chère d'un produit
     * 
     * USAGE : Affichage "À partir de X€"
     * 
     * @param productId ID du produit
     * @return Optional<Variant> variante au prix minimum
     */
//     @Query("SELECT v FROM Variant v WHERE v.product.id = :productId ORDER BY v.price ASC")
//     Optional<Variant> findCheapestVariantByProductId(@Param("productId") UUID productId);
    
    /**
     * Trouve la variante la plus chère d'un produit
     * 
     * @param productId ID du produit
     * @return Optional<Variant> variante au prix maximum
     */
//     @Query("SELECT v FROM Variant v WHERE v.product.id = :productId ORDER BY v.price DESC")
//     Optional<Variant> findMostExpensiveVariantByProductId(@Param("productId") UUID productId);
    
    /**
     * Vérifie si un SKU existe déjà
     * 
     * USAGE : Validation avant création de variante (SKU doit être unique)
     * 
     * @param sku SKU à vérifier
     * @return boolean true si le SKU existe déjà
     */
    boolean existsBySku(String sku);
    
    /**
     * Compte le nombre de variantes d'un produit
     * 
     * @param productId ID du produit
     * @return long nombre de variantes
     */
    long countByProductId(UUID productId);
    
    /**
     * Calcule le stock total d'un produit
     * 
     * @param productId ID du produit
     * @return long somme des stocks de toutes les variantes
     */
    @Query("SELECT COALESCE(SUM(v.stock), 0) FROM Variant v WHERE v.product.id = :productId")
    long sumStockByProductId(@Param("productId") UUID productId);
    
    /**
     * Met à jour le stock d'une variante (pour les commandes)
     * 
     * USAGE : Décrémenter le stock lors d'une vente
     * 
     * @param variantId ID de la variante
     * @param newStock nouveau stock
     * @return int nombre de lignes mises à jour
     */
    @Query("UPDATE Variant v SET v.stock = :newStock WHERE v.id = :variantId")
    int updateStock(@Param("variantId") UUID variantId, @Param("newStock") int newStock);
    
    /**
     * Trouve les variantes les plus vendues d'une boutique
     * 
     * USAGE : Statistiques vendeur, suggestions
     * Note : Nécessite la table OrderItem pour compter les ventes
     * 
     * @param shopId ID de la boutique
     * @param limit nombre de variantes à retourner
     * @return List<Variant> variantes les plus vendues
     */
    @Query("SELECT v FROM Variant v " +
           "WHERE v.product.shop.id = :shopId " +
           "ORDER BY (SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.variant.id = v.id) DESC")
    List<Variant> findBestSellingVariantsByShop(@Param("shopId") UUID shopId, 
                                               org.springframework.data.domain.Pageable pageable);
}