package com.example.e_souk.Model;

import jakarta.persistence.*;
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
 * Entité représentant un produit favori d'un utilisateur
 * Table de liaison entre User et Product pour gérer les favoris
 */
@Entity
@Table(name = "product_favorites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProductFavorite {
    
    /**
     * Identifiant unique du favori
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Date d'ajout aux favoris
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Utilisateur qui a ajouté le produit aux favoris
     * Relation ManyToOne : plusieurs favoris peuvent appartenir à un utilisateur
     * Fetch EAGER : on charge toujours l'utilisateur car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un favori, l'utilisateur est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "L'utilisateur est obligatoire")
    private User user;
    
    /**
     * Produit ajouté aux favoris
     * Relation ManyToOne : plusieurs favoris peuvent pointer vers un produit
     * Fetch EAGER : on charge toujours le produit car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un favori, le produit est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Le produit est obligatoire")
    private Product product;
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Récupère le nom d'utilisateur
     * @return Nom d'utilisateur
     */
    public String getUsername() {
        return user != null ? user.getUsername() : "";
    }
    
    /**
     * Récupère le nom du produit
     * @return Nom du produit
     */
    public String getProductName() {
        return product != null ? product.getName() : "";
    }
    
    /**
     * Récupère le nom de la boutique du produit
     * @return Nom de la boutique
     */
    public String getShopName() {
        if (product == null || product.getShop() == null) {
            return "";
        }
        return product.getShop().getBrandName();
    }
    
    /**
     * Récupère l'email de l'utilisateur
     * @return Email de l'utilisateur
     */
    public String getUserEmail() {
        return user != null ? user.getEmail() : "";
    }
    
    /**
     * Récupère la photo de profil de l'utilisateur
     * @return URL de la photo de profil
     */
    public String getUserPicture() {
        return user != null ? user.getPicture() : "";
    }
    
    /**
     * Récupère les images du produit
     * @return Tableau d'URLs des images du produit
     */
    public String getProductPicture() {
        return product != null ? product.getPicture() : "";
    }
    
    /**
     * Récupère la première image du produit
     * @return URL de la première image ou chaîne vide
     */
    public String getFirstProductPicture() {
        String pictures = getProductPicture();
        return pictures.length() > 0 ? pictures.split(",")[0] : "";
    }
    
    /**
     * Récupère le prix minimum du produit
     * @return Prix minimum parmi toutes les variantes
     */
    public Float getProductMinPrice() {
        return product != null ? product.getMinPrice() : 0.0f;
    }
    
    /**
     * Récupère le prix maximum du produit
     * @return Prix maximum parmi toutes les variantes
     */
    public Float getProductMaxPrice() {
        return product != null ? product.getMaxPrice() : 0.0f;
    }
    
    /**
     * Récupère la note moyenne du produit
     * @return Note moyenne du produit
     */
    public Double getProductAverageRating() {
        return product != null ? product.getAverageRating() : 0.0;
    }
    
    /**
     * Vérifie si le produit a du stock
     * @return true si le produit a du stock disponible
     */
    public Boolean isProductInStock() {
        return product != null && product.hasStock();
    }
    
    /**
     * Calcule la durée depuis l'ajout aux favoris en jours
     * @return Nombre de jours depuis l'ajout aux favoris
     */
    public Long getFavoriteDurationInDays() {
        if (addedAt == null) {
            return 0L;
        }
        
        return java.time.Duration.between(addedAt, LocalDateTime.now()).toDays();
    }
    
    /**
     * Vérifie si le favori est récent (moins de 7 jours)
     * @return true si le favori date de moins de 7 jours
     */
    public Boolean isRecentFavorite() {
        return getFavoriteDurationInDays() < 7;
    }
} 