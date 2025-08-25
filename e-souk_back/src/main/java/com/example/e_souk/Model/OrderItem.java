package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant un article dans une commande
 * Chaque article correspond à une variante de produit avec une quantité et un prix unitaire
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OrderItem {
    
    /**
     * Identifiant unique de l'article de commande
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Quantité commandée
     * Doit être supérieure à 0
     */
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins de 1")
    private Integer quantity;
    
    /**
     * Prix unitaire au moment de la commande
     * Permet de conserver le prix historique même si le prix change après
     */
    @Column(name = "unit_price", nullable = false)
    @NotNull(message = "Le prix unitaire est obligatoire")
    @Min(value = 0, message = "Le prix unitaire ne peut pas être négatif")
    private Float unitPrice;
    
    /**
     * Date de création de l'article de commande
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Commande à laquelle cet article appartient
     * Relation ManyToOne : plusieurs articles peuvent appartenir à une commande
     * Fetch EAGER : on charge toujours la commande car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un article, sa commande est aussi sauvegardée
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "commande_id", nullable = false)
    @NotNull(message = "La commande de l'article est obligatoire")
    private Commande commande;
    
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
     * @return Sous-total (prix unitaire * quantité)
     */
    public Float getSubTotal() {
        if (unitPrice == null || quantity == null) {
            return 0.0f;
        }
        return unitPrice * quantity;
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
     * Récupère le prix actuel de la variante
     * @return Prix actuel de la variante
     */
    public Float getCurrentPrice() {
        if (variant == null) {
            return 0.0f;
        }
        return variant.getProduct().getPrice();
    }
    
    /**
     * Vérifie si le prix a changé depuis la commande
     * @return true si le prix actuel est différent du prix de commande
     */
    public Boolean hasPriceChanged() {
        Float currentPrice = getCurrentPrice();
        return !currentPrice.equals(unitPrice);
    }
    
    /**
     * Calcule la différence de prix
     * @return Différence entre le prix actuel et le prix de commande
     */
    public Float getPriceDifference() {
        Float currentPrice = getCurrentPrice();
        return currentPrice - unitPrice;
    }
} 