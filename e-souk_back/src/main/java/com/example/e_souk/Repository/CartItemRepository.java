package com.example.e_souk.Repository;

import com.example.e_souk.Model.CartItem;
import com.example.e_souk.Model.Cart;
import com.example.e_souk.Model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour la gestion des articles du panier
 * Fournit les méthodes d'accès aux données pour les articles du panier
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    
    /**
     * Trouve tous les articles d'un panier
     * @param cart Panier
     * @return Liste des articles du panier
     */
    List<CartItem> findByCart(Cart cart);
    
    /**
     * Trouve tous les articles d'un panier par son ID
     * @param cartId ID du panier
     * @return Liste des articles du panier
     */
    List<CartItem> findByCartId(UUID cartId);
    
    /**
     * Trouve un article du panier par sa variante
     * @param cart Panier
     * @param variant Variante
     * @return Article du panier
     */
    Optional<CartItem> findByCartAndVariant(Cart cart, Variant variant);
    
    /**
     * Trouve un article du panier par l'ID du panier et l'ID de la variante
     * @param cartId ID du panier
     * @param variantId ID de la variante
     * @return Article du panier
     */
    Optional<CartItem> findByCartIdAndVariantId(UUID cartId, UUID variantId);
    
    /**
     * Supprime tous les articles d'un panier
     * @param cartId ID du panier
     */
    void deleteByCartId(UUID cartId);
    
    /**
     * Supprime un article du panier par sa variante
     * @param cartId ID du panier
     * @param variantId ID de la variante
     */
    void deleteByCartIdAndVariantId(UUID cartId, UUID variantId);
    
    /**
     * Compte le nombre d'articles dans un panier
     * @param cartId ID du panier
     * @return Nombre d'articles
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Long countByCartId(@Param("cartId") UUID cartId);
    
    /**
     * Calcule le montant total d'un panier
     * @param cartId ID du panier
     * @return Montant total
     */
    @Query("SELECT SUM(ci.quantity * ci.variant.product.price) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Double getTotalAmountByCartId(@Param("cartId") UUID cartId);
}
