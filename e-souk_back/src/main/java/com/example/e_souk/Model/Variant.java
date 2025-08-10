package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
 * Entité représentant une variante de produit dans la marketplace
 * Chaque variante a son propre SKU, prix et stock (ex: T-shirt Rouge Taille L)
 */
@Entity
@Table(name = "variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Variant {
    
    /**
     * Identifiant unique de la variante
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Code SKU unique de la variante
     * Stock Keeping Unit - identifiant unique pour la gestion des stocks
     * Doit être unique dans toute la plateforme
     */
    @Column(name = "sku", nullable = false, length = 50)
    @NotBlank(message = "Le SKU est obligatoire")
    @Size(min = 3, max = 50, message = "Le SKU doit contenir entre 3 et 50 caractères")
    private String sku;
    
    /**
     * Prix de la variante
     * Montant en euros avec 2 décimales
     */
    @Column(name = "price", nullable = false)
    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private Float price;
    
    /**
     * Stock disponible pour cette variante
     * Nombre d'unités en stock
     */
    @Column(name = "stock", nullable = false)
    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stock = 0;
    
    /**
     * Indique si la variante est active et visible
     * Permet de désactiver temporairement une variante sans la supprimer
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Date de création de la variante
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Date de dernière modification de la variante
     * Mise à jour automatiquement par Spring Data JPA
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Produit parent de cette variante
     * Relation ManyToOne : plusieurs variantes peuvent appartenir à un produit
     * Fetch EAGER : on charge toujours le produit car c'est une info importante
     * Cascade PERSIST : si on sauvegarde une variante, son produit est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Le produit de la variante est obligatoire")
    private Product product;
    
    /**
     * Valeurs d'attributs de cette variante
     * Relation OneToMany : une variante peut avoir plusieurs valeurs d'attributs (taille, couleur, etc.)
     * Cascade ALL : si on supprime une variante, ses valeurs d'attributs sont aussi supprimées
     * Fetch LAZY : on ne charge les valeurs que si nécessaire (optimisation performance)
     */
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AttributeValue> attributeValues = new ArrayList<>();
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Vérifie si la variante a du stock disponible
     * @return true si le stock est supérieur à 0
     */
    public Boolean isInStock() {
        return stock > 0;
    }
    
    /**
     * Vérifie si la variante a suffisamment de stock pour une quantité donnée
     * @param quantity Quantité demandée
     * @return true si le stock est suffisant
     */
    public Boolean hasEnoughStock(Integer quantity) {
        return stock >= quantity;
    }
    
    /**
     * Réduit le stock de la variante
     * @param quantity Quantité à retirer du stock
     * @return true si l'opération a réussi
     */
    public Boolean reduceStock(Integer quantity) {
        if (hasEnoughStock(quantity)) {
            this.stock -= quantity;
            return true;
        }
        return false;
    }
    
    /**
     * Augmente le stock de la variante
     * @param quantity Quantité à ajouter au stock
     */
    public void increaseStock(Integer quantity) {
        this.stock += quantity;
    }
    
    /**
     * Calcule le sous-total pour une quantité donnée
     * @param quantity Quantité
     * @return Sous-total (prix * quantité)
     */
    public Float getSubTotal(Integer quantity) {
        return price * quantity;
    }
    
    /**
     * Récupère la description des attributs de la variante
     * @return Description formatée des attributs (ex: "Rouge, Taille L")
     */
    public String getAttributeDescription() {
        if (attributeValues == null || attributeValues.isEmpty()) {
            return "";
        }
        
        return attributeValues.stream()
                .map(AttributeValue::getValue)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
    
    /**
     * Récupère la valeur d'un attribut spécifique
     * @param attributeName Nom de l'attribut recherché
     * @return Valeur de l'attribut ou null si non trouvé
     */
    public String getAttributeValue(String attributeName) {
        if (attributeValues == null) {
            return null;
        }
        
        return attributeValues.stream()
                .filter(av -> av.getAttribute().getName().equals(attributeName))
                .map(AttributeValue::getValue)
                .findFirst()
                .orElse(null);
    }
} 