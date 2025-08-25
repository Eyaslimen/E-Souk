package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entité représentant le panier d'un utilisateur dans la marketplace
 * Chaque utilisateur a exactement un panier qui contient plusieurs articles
 */
@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Cart {
    
    /**
     * Identifiant unique du panier
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Date de création du panier
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Date de dernière modification du panier
     * Mise à jour automatiquement par Spring Data JPA
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Utilisateur propriétaire du panier
     * Relation OneToOne : chaque utilisateur a exactement un panier
     * Fetch EAGER : on charge toujours l'utilisateur car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un panier, son utilisateur est aussi sauvegardé
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull(message = "L'utilisateur du panier est obligatoire")
    private User user;
    
    /**
     * Articles dans le panier
     * Relation OneToMany : un panier peut contenir plusieurs articles
     * Cascade ALL : si on supprime un panier, ses articles sont aussi supprimés
     * Fetch LAZY : on ne charge les articles que si nécessaire (optimisation performance)
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Calcule le montant total du panier
     * Inclut tous les articles avec leurs quantités et prix
     * @return Montant total du panier
     */
    // public Float getTotalAmount() {
    //     if (cartItems == null || cartItems.isEmpty()) {
    //         return 0.0f;
    //     }
        
    //     return (float) cartItems.stream()
    //             .mapToDouble(item -> item.getSubTotal())
    //             .sum();
    // }
    
    /**
     * Compte le nombre total d'articles dans le panier
     * @return Nombre total d'articles (somme des quantités)
     */
    public Integer getItemCount() {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0;
        }
        
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    /**
     * Compte le nombre de types d'articles différents dans le panier
     * @return Nombre de types d'articles différents
     */
    public Long getUniqueItemCount() {
        if (cartItems == null) {
            return 0L;
        }
        
        return cartItems.stream().count();
    }
    
    /**
     * Vérifie si le panier est vide
     * @return true si le panier ne contient aucun article
     */
    public Boolean isEmpty() {
        return cartItems == null || cartItems.isEmpty();
    }
    
    /**
     * Vérifie si le panier contient des articles
     * @return true si le panier contient au moins un article
     */
    public Boolean hasItems() {
        return !isEmpty();
    }
    
    /**
     * Récupère un article du panier par sa variante
     * @param variantId ID de la variante recherchée
     * @return Article du panier ou null si non trouvé
     */
    public CartItem getItemByVariant(UUID variantId) {
        if (cartItems == null) {
            return null;
        }
        
        return cartItems.stream()
                .filter(item -> item.getVariant().getId().equals(variantId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Ajoute un article au panier ou met à jour la quantité si l'article existe déjà
     * @param variant Variante à ajouter
     * @param quantity Quantité à ajouter
     */
    public void addItem(Variant variant, Integer quantity) {
        CartItem existingItem = getItemByVariant(variant.getId());
        
        if (existingItem != null) {
            // Mise à jour de la quantité si l'article existe déjà
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Création d'un nouvel article
            CartItem newItem = CartItem.builder()
                    .cart(this)
                    .variant(variant)
                    .quantity(quantity)
                    .build();
            cartItems.add(newItem);
        }
    }
    
    /**
     * Supprime un article du panier
     * @param variantId ID de la variante à supprimer
     * @return true si l'article a été supprimé
     */
    public Boolean removeItem(UUID variantId) {
        if (cartItems == null) {
            return false;
        }
        
        return cartItems.removeIf(item -> item.getVariant().getId().equals(variantId));
    }
    
    /**
     * Vide complètement le panier
     */
    public void clear() {
        if (cartItems != null) {
            cartItems.clear();
        }
    }
} 