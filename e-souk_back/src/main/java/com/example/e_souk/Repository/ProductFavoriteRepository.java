package com.example.e_souk.Repository;

import com.example.e_souk.Model.ProductFavorite;
import com.example.e_souk.Model.User;
import com.example.e_souk.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des produits favoris
 * Fournit les méthodes d'accès aux données pour les favoris
 */
@Repository
public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, UUID> {
    
    /**
     * Trouve tous les favoris d'un utilisateur
     * @param user Utilisateur
     * @return Liste des favoris de l'utilisateur
     */
    List<ProductFavorite> findByUser(User user);
    
    /**
     * Trouve tous les favoris d'un utilisateur par son ID
     * @param userId ID de l'utilisateur
     * @return Liste des favoris de l'utilisateur
     */
    List<ProductFavorite> findByUserId(UUID userId);
    
    /**
     * Trouve un favori par l'utilisateur et le produit
     * @param user Utilisateur
     * @param product Produit
     * @return Favori trouvé
     */
    Optional<ProductFavorite> findByUserAndProduct(User user, Product product);
    
    /**
     * Trouve un favori par l'ID de l'utilisateur et l'ID du produit
     * @param userId ID de l'utilisateur
     * @param productId ID du produit
     * @return Favori trouvé
     */
    Optional<ProductFavorite> findByUserIdAndProductId(UUID userId, UUID productId);
    
    /**
     * Vérifie si un produit est en favori pour un utilisateur
     * @param userId ID de l'utilisateur
     * @param productId ID du produit
     * @return true si le produit est en favori
     */
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);
    
    /**
     * Supprime un favori par l'utilisateur et le produit
     * @param user Utilisateur
     * @param product Produit
     */
    void deleteByUserAndProduct(User user, Product product);
    
    /**
     * Supprime un favori par l'ID de l'utilisateur et l'ID du produit
     * @param userId ID de l'utilisateur
     * @param productId ID du produit
     */
    void deleteByUserIdAndProductId(UUID userId, UUID productId);
    
    /**
     * Compte le nombre de favoris d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Nombre de favoris
     */
    @Query("SELECT COUNT(pf) FROM ProductFavorite pf WHERE pf.user.id = :userId")
    Long countByUserId(@Param("userId") UUID userId);
    
    /**
     * Trouve les favoris d'un utilisateur avec les informations du produit
     * @param userId ID de l'utilisateur
     * @return Liste des favoris avec produits
     */
    @Query("SELECT pf FROM ProductFavorite pf JOIN FETCH pf.product p JOIN FETCH p.shop WHERE pf.user.id = :userId ORDER BY pf.addedAt DESC")
    List<ProductFavorite> findByUserIdWithProductAndShop(@Param("userId") UUID userId);
}
