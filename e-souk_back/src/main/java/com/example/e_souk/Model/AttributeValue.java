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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant une valeur d'attribut pour une variante
 * Fait le lien entre une variante et un attribut avec sa valeur spécifique
 * Exemple: Variant "T-shirt Rouge Taille L" -> AttributeValue "Couleur: Rouge", "Taille: L"
 */
@Entity
@Table(name = "attribute_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AttributeValue {
    
    /**
     * Identifiant unique de la valeur d'attribut
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Valeur de l'attribut
     * Exemple: "Rouge", "L", "Coton", etc.
     */
    @Column(name = "value", nullable = false, length = 100)
    @NotBlank(message = "La valeur de l'attribut est obligatoire")
    @Size(max = 100, message = "La valeur ne peut pas dépasser 100 caractères")
    private String value;
    
    /**
     * Date de création de la valeur d'attribut
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Variante à laquelle cette valeur d'attribut appartient
     * Relation ManyToOne : plusieurs valeurs d'attributs peuvent appartenir à une variante
     * Fetch EAGER : on charge toujours la variante car c'est une info importante
     * Cascade PERSIST : si on sauvegarde une valeur d'attribut, sa variante est aussi sauvegardée
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "variant_id", nullable = false)
    @NotNull(message = "La variante est obligatoire")
    private Variant variant;
    
    /**
     * Attribut auquel cette valeur correspond
     * Relation ManyToOne : plusieurs valeurs peuvent correspondre à un attribut
     * Fetch EAGER : on charge toujours l'attribut car c'est une info importante
     * Cascade PERSIST : si on sauvegarde une valeur d'attribut, son attribut est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "attribute_id", nullable = false)
    @NotNull(message = "L'attribut est obligatoire")
    private Attribute attribute;
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Récupère le nom de l'attribut
     * @return Nom de l'attribut (ex: "Couleur", "Taille")
     */
    public String getAttributeName() {
        return attribute != null ? attribute.getName() : "";
    }
    
    /**
     * Récupère le type de l'attribut
     * @return Type de l'attribut (ex: "TEXT", "NUMBER", "COLOR")
     */
    public String getAttributeType() {
        return attribute != null ? attribute.getType() : "";
    }
    
    /**
     * Vérifie si l'attribut est obligatoire
     * @return true si l'attribut est obligatoire
     */
    public Boolean isAttributeRequired() {
        return attribute != null && attribute.getIsRequired();
    }
    
    /**
     * Récupère une description formatée de la valeur d'attribut
     * @return Description au format "Nom: Valeur" (ex: "Couleur: Rouge")
     */
    public String getFormattedValue() {
        String attributeName = getAttributeName();
        if (attributeName.isEmpty()) {
            return value;
        }
        return attributeName + ": " + value;
    }
} 