package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant un article dans le panier d'un utilisateur
 * Chaque article correspond à une variante de produit avec une quantité spécifique
 */
@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CartItem {
    
    /**
     * Identifiant unique de l'article du panier
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Quantité de l'article dans le panier
     * Doit être supérieure à 0
     */
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins de 1")
    private Integer quantity;
    
    /**
     * Date d'ajout de l'article au panier
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;
    
    /**
     * Date de dernière modification de l'article
     * Mise à jour automatiquement par Spring Data JPA
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Panier auquel cet article appartient
     * Relation ManyToOne : plusieurs articles peuvent appartenir à un panier
     * Fetch EAGER : on charge toujours le panier car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un article, son panier est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "cart_id", nullable = false)
    @NotNull(message = "Le panier de l'article est obligatoire")
    private Cart cart;
    
    /**
     * Variante de produit correspondant à cet article
     * Relation ManyToOne : plusieurs articles peuvent correspondre à une variante
     * Fetch EAGER : on charge toujours la variante car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un article, sa variante est aussi sauvegardée
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "variant_id", nullable = false)
    @NotNull(message = "La variante de l'article est obligatoire")
    private Variant variant;
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Calcule le sous-total de cet article
     * @return Sous-total (prix de la variante * quantité)
     */
    // public Float getSubTotal() {
    //     if (variant == null || quantity == null) {
    //         return 0.0f;
    //     }
    //     return variant.getPrice() * quantity;
    // }
    
    /**
     * Vérifie si la variante a suffisamment de stock pour la quantité demandée
     * @return true si le stock est suffisant
     */
    public Boolean hasEnoughStock() {
        if (variant == null || quantity == null) {
            return false;
        }
        return variant.hasEnoughStock(quantity);
    }
    
    /**
     * Récupère le nom du produit de cet article
     * @return Nom du produit
     */
    public String getProductName() {
        if (variant == null || variant.getProduct() == null) {
            return "";
        }
        return variant.getProduct().getName();
    }
    
    /**
     * Récupère le nom de la boutique de cet article
     * @return Nom de la boutique
     */
    public String getShopName() {
        if (variant == null || variant.getProduct() == null || variant.getProduct().getShop() == null) {
            return "";
        }
        return variant.getProduct().getShop().getBrandName();
    }
    
    /**
     * Récupère la description des attributs de la variante
     * @return Description formatée des attributs (ex: "Rouge, Taille L")
     */
    public String getVariantDescription() {
        if (variant == null) {
            return "";
        }
        return variant.getAttributeDescription();
    }
    
    /**
     * Récupère le SKU de la variante
     * @return SKU de la variante
     */
    public String getSku() {
        if (variant == null) {
            return "";
        }
        return variant.getSku();
    }
    
    /**
     * Récupère le prix unitaire de la variante
     * @return Prix unitaire
     */
    // public Float getUnitPrice() {
    //     if (variant == null) {
    //         return 0.0f;
    //     }
    //     return variant.getPrice();
    // }
    
    /**
     * Augmente la quantité de l'article
     * @param additionalQuantity Quantité à ajouter
     */
    public void increaseQuantity(Integer additionalQuantity) {
        if (additionalQuantity != null && additionalQuantity > 0) {
            this.quantity += additionalQuantity;
        }
    }
    
    /**
     * Diminue la quantité de l'article
     * @param reducedQuantity Quantité à retirer
     * @return true si l'opération a réussi
     */
    public Boolean decreaseQuantity(Integer reducedQuantity) {
        if (reducedQuantity != null && reducedQuantity > 0 && this.quantity >= reducedQuantity) {
            this.quantity -= reducedQuantity;
            return true;
        }
        return false;
    }
    
    /**
     * Met à jour la quantité de l'article
     * @param newQuantity Nouvelle quantité
     * @return true si la quantité est valide
     */
    public Boolean setQuantity(Integer newQuantity) {
        if (newQuantity != null && newQuantity > 0) {
            this.quantity = newQuantity;
            return true;
        }
        return false;
    }
} 