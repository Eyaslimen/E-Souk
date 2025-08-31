package com.example.e_souk.Repository;

import com.example.e_souk.Model.Cart;
import com.example.e_souk.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des paniers
 * Fournit les méthodes d'accès aux données pour les paniers
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    
    /**
     * Trouve le panier d'un utilisateur
     * @param user Utilisateur propriétaire du panier
     * @return Panier de l'utilisateur
     */
    Optional<Cart> findByUser(User user);
    
    /**
     * Trouve le panier d'un utilisateur par son ID
     * @param userId ID de l'utilisateur
     * @return Panier de l'utilisateur
     */
    Optional<Cart> findByUserId(UUID userId);
    
    /**
     * Vérifie si un utilisateur a un panier
     * @param userId ID de l'utilisateur
     * @return true si l'utilisateur a un panier
     */
    boolean existsByUserId(UUID userId);
    
    /**
     * Supprime le panier d'un utilisateur
     * @param userId ID de l'utilisateur
     */
    void deleteByUserId(UUID userId);
    
    /**
     * Compte le nombre d'articles dans le panier d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Nombre d'articles dans le panier
     */
    @Query("SELECT COUNT(ci) FROM Cart c JOIN c.cartItems ci WHERE c.user.id = :userId")
    Long countItemsByUserId(@Param("userId") UUID userId);
}
