
package com.example.e_souk.Repository;

import com.example.e_souk.Model.Shop;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
 * Repository pour gérer les opérations CRUD sur les boutiques
 */
@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {
    
    /**
     * Trouve une boutique par son nom de marque (insensible à la casse)
     * 
     * POURQUOI cette méthode ?
     * - Éviter les doublons de noms de boutiques
     * - Validation métier importante
     * 
     * @param brandName nom de la marque
     * @return Optional<Shop> - peut être vide si pas trouvé
     */
    @Query("SELECT s FROM Shop s WHERE LOWER(s.brandName) = LOWER(:brandName)")
    Optional<Shop> findByBrandNameIgnoreCase(@Param("brandName") String brandName);
    
    /**
     * Trouve toutes les boutiques d'un propriétaire (user)
     * 
     * RÈGLE MÉTIER : Un user peut-il avoir plusieurs boutiques ?
     * - Pour l'instant on autorise (flexibilité future)
     * - Mais généralement 1 user = 1 boutique
     * 
     * @param ownerId ID du propriétaire
     * @return List<Shop> boutiques de ce propriétaire
     */
    @Query("SELECT s FROM Shop s WHERE s.owner.id = :ownerId")
    List<Shop> findByOwnerId(@Param("ownerId") UUID ownerId);
    
    /**
     * Trouve la boutique active d'un propriétaire
     * 
     * POURQUOI seulement la boutique active ?
     * - Un user peut désactiver temporairement sa boutique
     * - On ne veut que les boutiques opérationnelles
     * 
     * @param ownerId ID du propriétaire
     * @return Optional<Shop> la boutique active
     */
    @Query("SELECT s FROM Shop s WHERE s.owner.id = :ownerId AND s.isActive = true")
    Optional<Shop> findActiveShopByOwnerId(@Param("ownerId") UUID ownerId);
    
    /**
     * Recherche des boutiques par nom de marque (pour l'auto-complétion)
     * 
     * @param searchTerm terme de recherche
     * @return List<Shop> boutiques correspondantes
     */
    @Query("SELECT s FROM Shop s WHERE LOWER(s.brandName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND s.isActive = true")
    List<Shop> findByBrandNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    /**
     * Trouve toutes les boutiques actives avec pagination
     * 
     * @param pageable pagination
     * @return Page<Shop> page de boutiques actives
     */
    @Query("SELECT s FROM Shop s WHERE s.isActive = true")
    Page<Shop> findAllActiveShops(Pageable pageable);
    
    /**
     * Compte le nombre de produits dans une boutique
     * 
     * POURQUOI cette méthode ?
     * - Statistiques pour le propriétaire
     * - Savoir si on peut supprimer une boutique
     * 
     * @param shopId ID de la boutique
     * @return long nombre de produits
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.shop.id = :shopId")
    long countProductsInShop(@Param("shopId") UUID shopId);
    
    /**
     * Compte le nombre de commandes reçues par une boutique
     * 
     * @param shopId ID de la boutique
     * @return long nombre de commandes
     */
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.shop.id = :shopId")
    long countOrdersInShop(@Param("shopId") UUID shopId);
    
    /**
     * Trouve les boutiques les plus suivies (pour la page d'accueil)
     * 
     * @param pageable pagination
     * @return Page<Shop> boutiques populaires
     */
    @Query("SELECT s FROM Shop s WHERE s.isActive = true ORDER BY " +
           "(SELECT COUNT(sf) FROM ShopFollower sf WHERE sf.shop.id = s.id) DESC")
    Page<Shop> findMostFollowedShops(Pageable pageable);
    
    /**
     * Vérifie si un user a déjà une boutique active
     * 
     * RÈGLE MÉTIER : Un user ne peut avoir qu'une seule boutique active
     * 
     * @param ownerId ID du propriétaire
     * @return boolean true si le user a déjà une boutique active
     */
    @Query("SELECT COUNT(s) > 0 FROM Shop s WHERE s.owner.id = :ownerId AND s.isActive = true")
    boolean existsActiveShopByOwnerId(@Param("ownerId") UUID ownerId);
}