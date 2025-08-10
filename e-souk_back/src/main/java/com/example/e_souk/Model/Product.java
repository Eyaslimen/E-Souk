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
 * Entité représentant un produit dans la marketplace
 * Chaque produit appartient à une boutique et une catégorie, et peut avoir plusieurs variantes
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product {
    
    /**
     * Identifiant unique du produit
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Nom du produit
     * Doit être non vide et unique dans la boutique
     */
    @Column(name = "name", nullable = false, length = 200)
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 200, message = "Le nom doit contenir entre 2 et 200 caractères")
    private String name;
    
    /**
     * Description détaillée du produit
     * Permet de présenter le produit aux clients
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
    private String description;
    
    /**
     * Images du produit
     * Stockées comme un tableau de URLs séparées par des virgules
     * PostgreSQL peut gérer les tableaux directement
     */
    @Column(name = "picture")
    private String picture;
    
    /**
     * Indique si le produit est actif et visible
     * Permet de désactiver temporairement un produit sans le supprimer
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Date de création du produit
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Date de dernière modification du produit
     * Mise à jour automatiquement par Spring Data JPA
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Catégorie du produit
     * Relation ManyToOne : plusieurs produits peuvent appartenir à une catégorie
     * Fetch EAGER : on charge toujours la catégorie car c'est une info importante pour l'affichage
     * Cascade PERSIST : si on sauvegarde un produit, sa catégorie est aussi sauvegardée
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "La catégorie du produit est obligatoire")
    private Category category;
    
    /**
     * Boutique qui vend ce produit
     * Relation ManyToOne : plusieurs produits peuvent appartenir à une boutique
     * Fetch EAGER : on charge toujours la boutique car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un produit, sa boutique est aussi sauvegardée
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "shop_id", nullable = false)
    @NotNull(message = "La boutique du produit est obligatoire")
    private Shop shop;
    
    /**
     * Variantes de ce produit
     * Relation OneToMany : un produit peut avoir plusieurs variantes (taille, couleur, etc.)
     * Cascade ALL : si on supprime un produit, ses variantes sont aussi supprimées
     * Fetch LAZY : on ne charge les variantes que si nécessaire (optimisation performance)
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Variant> variants = new ArrayList<>();
    
    /**
     * Avis reçus par ce produit
     * Relation OneToMany : un produit peut recevoir plusieurs avis
     * Cascade PERSIST : si on sauvegarde un produit, ses avis sont aussi sauvegardés
     * Fetch LAZY : on ne charge les avis que si nécessaire
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Calcule la note moyenne du produit basée sur tous les avis
     * @return Note moyenne ou 0.0 si aucun avis
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
     * Calcule le stock total du produit en additionnant tous les stocks des variantes actives
     * @return Stock total disponible
     */
    public Integer getTotalStock() {
        if (variants == null || variants.isEmpty()) {
            return 0;
        }
        
        return variants.stream()
                .filter(Variant::getIsActive)
                .mapToInt(Variant::getStock)
                .sum();
    }
    
    /**
     * Vérifie si le produit a du stock disponible
     * @return true si au moins une variante a du stock
     */
    public Boolean hasStock() {
        return getTotalStock() > 0;
    }
    
    /**
     * Récupère le prix minimum parmi toutes les variantes actives
     * @return Prix minimum ou null si aucune variante
     */
    public Float getMinPrice() {
        if (variants == null || variants.isEmpty()) {
            return null;
        }
        
        return variants.stream()
                .filter(Variant::getIsActive)
                .map(Variant::getPrice)
                .min(Float::compareTo)
                .orElse(null);
    }
    
    /**
     * Récupère le prix maximum parmi toutes les variantes actives
     * @return Prix maximum ou null si aucune variante
     */
    public Float getMaxPrice() {
        if (variants == null || variants.isEmpty()) {
            return null;
        }
        
        return variants.stream()
                .filter(Variant::getIsActive)
                .map(Variant::getPrice)
                .max(Float::compareTo)
                .orElse(null);
    }
    
    /**
     * Récupère le nombre de variantes actives
     * @return Nombre de variantes actives
     */
    public Long getActiveVariantsCount() {
        if (variants == null) {
            return 0L;
        }
        
        return variants.stream()
                .filter(Variant::getIsActive)
                .count();
    }
    
    /**
     * Récupère le nombre total d'avis
     * @return Nombre total d'avis
     */
    public Long getReviewsCount() {
        if (reviews == null) {
            return 0L;
        }
        
        return (long) reviews.size();
    }
} 