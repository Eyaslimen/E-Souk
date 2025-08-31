package com.example.e_souk.Repository;

import com.example.e_souk.Model.ShopFollower;
import com.example.e_souk.Model.User;
import com.example.e_souk.Model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des followers des boutiques
 * Fournit les méthodes d'accès aux données pour les followers
 */
@Repository
public interface ShopFollowerRepository extends JpaRepository<ShopFollower, UUID> {
    
    /**
     * Trouve tous les followers d'une boutique
     * @param shop Boutique
     * @return Liste des followers de la boutique
     */
    List<ShopFollower> findByShop(Shop shop);
    
    /**
     * Trouve tous les followers d'une boutique par son ID
     * @param shopId ID de la boutique
     * @return Liste des followers de la boutique
     */
    List<ShopFollower> findByShopId(UUID shopId);
    
    /**
     * Trouve toutes les boutiques suivies par un utilisateur
     * @param user Utilisateur
     * @return Liste des boutiques suivies par l'utilisateur
     */
    List<ShopFollower> findByUser(User user);
    
    /**
     * Trouve toutes les boutiques suivies par un utilisateur par son ID
     * @param userId ID de l'utilisateur
     * @return Liste des boutiques suivies par l'utilisateur
     */
    List<ShopFollower> findByUserId(UUID userId);
    
    /**
     * Trouve un follower par l'utilisateur et la boutique
     * @param user Utilisateur
     * @param shop Boutique
     * @return Follower trouvé
     */
    Optional<ShopFollower> findByUserAndShop(User user, Shop shop);
    
    /**
     * Trouve un follower par l'ID de l'utilisateur et l'ID de la boutique
     * @param userId ID de l'utilisateur
     * @param shopId ID de la boutique
     * @return Follower trouvé
     */
    Optional<ShopFollower> findByUserIdAndShopId(UUID userId, UUID shopId);
    
    /**
     * Vérifie si un utilisateur suit une boutique
     * @param userId ID de l'utilisateur
     * @param shopId ID de la boutique
     * @return true si l'utilisateur suit la boutique
     */
    boolean existsByUserIdAndShopId(UUID userId, UUID shopId);
    
    /**
     * Supprime un follower par l'utilisateur et la boutique
     * @param user Utilisateur
     * @param shop Boutique
     */
    void deleteByUserAndShop(User user, Shop shop);
    
    /**
     * Supprime un follower par l'ID de l'utilisateur et l'ID de la boutique
     * @param userId ID de l'utilisateur
     * @param shopId ID de la boutique
     */
    void deleteByUserIdAndShopId(UUID userId, UUID shopId);
    
    /**
     * Compte le nombre de followers d'une boutique
     * @param shopId ID de la boutique
     * @return Nombre de followers
     */
    @Query("SELECT COUNT(sf) FROM ShopFollower sf WHERE sf.shop.id = :shopId")
    Long countByShopId(@Param("shopId") UUID shopId);
    
    /**
     * Compte le nombre de boutiques suivies par un utilisateur
     * @param userId ID de l'utilisateur
     * @return Nombre de boutiques suivies
     */
    @Query("SELECT COUNT(sf) FROM ShopFollower sf WHERE sf.user.id = :userId")
    Long countByUserId(@Param("userId") UUID userId);
    
    /**
     * Trouve les boutiques suivies par un utilisateur avec les informations de la boutique
     * @param userId ID de l'utilisateur
     * @return Liste des boutiques suivies avec détails
     */
    @Query("SELECT sf FROM ShopFollower sf JOIN FETCH sf.shop s WHERE sf.user.id = :userId ORDER BY sf.followedAt DESC")
    List<ShopFollower> findByUserIdWithShop(@Param("userId") UUID userId);
    
    /**
     * Trouve les followers d'une boutique avec les informations de l'utilisateur
     * @param shopId ID de la boutique
     * @return Liste des followers avec détails utilisateur
     */
    @Query("SELECT sf FROM ShopFollower sf JOIN FETCH sf.user u WHERE sf.shop.id = :shopId ORDER BY sf.followedAt DESC")
    List<ShopFollower> findByShopIdWithUser(@Param("shopId") UUID shopId);
}
