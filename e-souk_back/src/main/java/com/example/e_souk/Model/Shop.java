package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
 * Entité représentant une boutique dans la marketplace
 * Chaque boutique appartient à un vendeur et peut contenir plusieurs produits
 */
@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Shop {
    
    /**
     * Identifiant unique de la boutique
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Nom de la marque/boutique
     * Doit être unique et non vide
     */
    @Column(name = "brand_name", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Le nom de la marque est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom de la marque doit contenir entre 2 et 100 caractères")
    private String brandName;
    
    /**
     * Description détaillée de la boutique
     * Permet de présenter la boutique aux clients
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    private String description;
    
    /**
     * Logo de la boutique
     * URL vers l'image du logo
     */
    @Column(name = "logo_picture", length = 255)
    @Size(max = 255, message = "L'URL du logo ne peut pas dépasser 255 caractères")
    private String logoPicture;
    
    /**
     * Frais de livraison de la boutique
     * Montant fixe pour toutes les commandes de cette boutique
     */
    @Column(name = "delivery_fee", nullable = false)
    @NotNull(message = "Les frais de livraison sont obligatoires")
    private Float deliveryFee = 0.0f;
    
    /**
     * Adresse de la boutique
     * Utilisée pour les informations de contact
     */
    @Column(name = "address", length = 255)
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String address;
    
    /**
     * Indique si la boutique est active et visible
     * Permet de désactiver temporairement une boutique sans la supprimer
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Date de création de la boutique
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Date de dernière modification de la boutique
     * Mise à jour automatiquement par Spring Data JPA
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Propriétaire de la boutique
     * Relation ManyToOne : plusieurs boutiques peuvent appartenir à un utilisateur
     * Fetch EAGER : on charge toujours le propriétaire car c'est une info importante
     * Cascade PERSIST : si on sauvegarde une boutique, le propriétaire est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id", nullable = false)
    @NotNull(message = "Le propriétaire de la boutique est obligatoire")
    private User owner;
    
    /**
     * Produits de cette boutique
     * Relation OneToMany : une boutique peut contenir plusieurs produits
     * Cascade PERSIST : si on sauvegarde une boutique, ses produits sont aussi sauvegardés
     * Fetch LAZY : on ne charge les produits que si nécessaire (optimisation performance)
     */
    @OneToMany(mappedBy = "shop", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
    
    /**
     * Commandes reçues par cette boutique
     * Relation OneToMany : une boutique peut recevoir plusieurs commandes
     * Cascade PERSIST : si on sauvegarde une boutique, ses commandes sont aussi sauvegardées
     * Fetch LAZY : on ne charge les commandes que si nécessaire
     */
    @OneToMany(mappedBy = "shop", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Commande> commandes = new ArrayList<>();
    
    /**
     * Avis reçus par cette boutique
     * Relation OneToMany : une boutique peut recevoir plusieurs avis
     * Cascade PERSIST : si on sauvegarde une boutique, ses avis sont aussi sauvegardés
     * Fetch LAZY : on ne charge les avis que si nécessaire
     */
    @OneToMany(mappedBy = "shop", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Récupère les statistiques de la boutique
     * @return Statistiques de la boutique (à implémenter)
     */
    public ShopStatistics getStatistics() {
        // Cette méthode sera implémentée quand on aura la classe ShopStatistics
        return null;
    }
    
    /**
     * Récupère les commandes récentes de la boutique
     * @return Liste des commandes triées par date (plus récentes en premier)
     */
    public List<Commande> getRecentOrders() {
        return commandes.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .limit(10) // Limite à 10 commandes récentes
                .toList();
    }
    
    /**
     * Récupère les produits populaires de la boutique
     * @return Liste des produits les plus vendus
     */
    public List<Product> getPopularProducts() {
        // Cette méthode sera implémentée avec la logique de calcul de popularité
        return products.stream()
                .filter(Product::getIsActive)
                .limit(10) // Limite à 10 produits populaires
                .toList();
    }
    
    /**
     * Calcule la note moyenne de la boutique
     * @return Note moyenne basée sur tous les avis
     */
    public Double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Compte le nombre total de produits actifs
     * @return Nombre de produits actifs dans la boutique
     */
    public Long getActiveProductsCount() {
        return products.stream()
                .filter(Product::getIsActive)
                .count();
    }
}

/**
 * Classe pour les statistiques de boutique
 * À implémenter selon les besoins métier
 */
class ShopStatistics {
    // À implémenter selon les besoins
} 