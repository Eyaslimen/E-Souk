package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
 * Entité représentant un avis dans la marketplace
 * Peut être un avis sur une boutique ou sur un produit spécifique
 */
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Review {
    
    /**
     * Identifiant unique de l'avis
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Note de l'avis (1 à 5 étoiles)
     * Doit être comprise entre 1 et 5
     */
    @Column(name = "rating", nullable = false)
    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note doit être au moins de 1")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    private Integer rating;
    
    /**
     * Commentaire de l'avis
     * Texte détaillé de l'avis
     */
    @Column(name = "comment", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Le commentaire ne peut pas dépasser 2000 caractères")
    private String comment;
    
    /**
     * Type d'avis (boutique ou produit)
     * Définit si l'avis porte sur une boutique ou un produit
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    @NotNull(message = "Le type d'avis est obligatoire")
    private ReviewType type;
    
    /**
     * Date de création de l'avis
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Date de dernière modification de l'avis
     * Mise à jour automatiquement par Spring Data JPA
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Utilisateur qui a écrit l'avis
     * Relation ManyToOne : plusieurs avis peuvent être écrits par un utilisateur
     * Fetch EAGER : on charge toujours l'utilisateur car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un avis, l'utilisateur est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "L'utilisateur de l'avis est obligatoire")
    private User user;
    
    /**
     * Boutique évaluée (si type = SHOP)
     * Relation ManyToOne : plusieurs avis peuvent être écrits sur une boutique
     * Fetch EAGER : on charge toujours la boutique car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un avis, la boutique est aussi sauvegardée
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "shop_id")
    private Shop shop;
    
    /**
     * Produit évalué (si type = PRODUCT)
     * Relation ManyToOne : plusieurs avis peuvent être écrits sur un produit
     * Fetch EAGER : on charge toujours le produit car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un avis, le produit est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_id")
    private Product product;
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Vérifie si l'avis est valide selon son type
     * @return true si l'avis a les bonnes relations selon son type
     */
    public Boolean isValid() {
        if (type == ReviewType.SHOP) {
            return shop != null && product == null;
        } else if (type == ReviewType.PRODUCT) {
            return product != null && shop == null;
        }
        return false;
    }
    
    /**
     * Récupère le nom de l'utilisateur qui a écrit l'avis
     * @return Nom d'utilisateur
     */
    public String getAuthorName() {
        return user != null ? user.getUsername() : "";
    }
    
    /**
     * Récupère la photo de profil de l'utilisateur
     * @return URL de la photo de profil
     */
    public String getAuthorPicture() {
        return user != null ? user.getPicture() : "";
    }
    
    /**
     * Récupère le nom de la boutique évaluée
     * @return Nom de la boutique ou chaîne vide
     */
    public String getShopName() {
        return shop != null ? shop.getBrandName() : "";
    }
    
    /**
     * Récupère le nom du produit évalué
     * @return Nom du produit ou chaîne vide
     */
    public String getProductName() {
        return product != null ? product.getName() : "";
    }
    
    /**
     * Récupère le nom de la boutique du produit évalué
     * @return Nom de la boutique du produit ou chaîne vide
     */
    public String getProductShopName() {
        if (product != null && product.getShop() != null) {
            return product.getShop().getBrandName();
        }
        return "";
    }
    
    /**
     * Récupère le titre de l'avis basé sur son type
     * @return Titre formaté de l'avis
     */
    public String getReviewTitle() {
        if (type == ReviewType.SHOP) {
            return "Avis sur " + getShopName();
        } else if (type == ReviewType.PRODUCT) {
            return "Avis sur " + getProductName();
        }
        return "Avis";
    }
    
    /**
     * Récupère la description de l'objet évalué
     * @return Description de l'objet évalué
     */
    public String getReviewedObjectDescription() {
        if (type == ReviewType.SHOP) {
            return shop != null ? shop.getDescription() : "";
        } else if (type == ReviewType.PRODUCT) {
            return product != null ? product.getDescription() : "";
        }
        return "";
    }
    
    /**
     * Calcule la durée depuis la création de l'avis en jours
     * @return Nombre de jours depuis la création
     */
    public Long getReviewAgeInDays() {
        if (createdAt == null) {
            return 0L;
        }
        
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
    }
    
    /**
     * Vérifie si l'avis est récent (moins de 30 jours)
     * @return true si l'avis date de moins de 30 jours
     */
    public Boolean isRecentReview() {
        return getReviewAgeInDays() < 30;
    }
    
    /**
     * Vérifie si l'avis a été modifié
     * @return true si l'avis a été modifié après sa création
     */
    public Boolean hasBeenModified() {
        return updatedAt != null && !updatedAt.equals(createdAt);
    }
    
    /**
     * Récupère une représentation textuelle de la note
     * @return Note en étoiles (ex: "★★★★☆")
     */
    public String getRatingStars() {
        if (rating == null) {
            return "☆☆☆☆☆";
        }
        
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }
} 