package com.example.e_souk.Repository;

import com.example.e_souk.Model.AttributeValue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository pour gérer les valeurs d'attributs
 * 
 * CONCEPT : AttributeValue = pont entre Variant et Attribute
 * Ex: Variante "T-shirt S Rouge" aura 2 AttributeValue :
 * - AttributeValue(attribute="Taille", value="S", variant=thisVariant)
 * - AttributeValue(attribute="Couleur", value="Rouge", variant=thisVariant)
 */
@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, UUID> {
    
    /**
     * Trouve toutes les valeurs d'attribut d'une variante
     * 
     * USAGE : Afficher les caractéristiques d'une variante
     * Ex: Pour variante ID=123 → ["Taille: S", "Couleur: Rouge"]
     * 
     * @param variantId ID de la variante
     * @return List<AttributeValue> valeurs de cette variante
     */
    @Query("SELECT av FROM AttributeValue av WHERE av.variant.id = :variantId")
    List<AttributeValue> findByVariantId(@Param("variantId") UUID variantId);
    
    /**
     * Trouve toutes les valeurs d'un attribut spécifique pour un produit
     * 
     * USAGE : Afficher toutes les tailles disponibles pour un produit
     * Ex: Pour produit "T-shirt" et attribut "Taille" → ["S", "M", "L", "XL"]
     * 
     * @param productId ID du produit
     * @param attributeId ID de l'attribut
     * @return List<String> valeurs distinctes de cet attribut pour ce produit
     */
    @Query("SELECT DISTINCT av.value FROM AttributeValue av " +
           "JOIN Variant v ON av.variant.id = v.id " +
           "WHERE v.product.id = :productId AND av.attribute.id = :attributeId " +
           "ORDER BY av.value")
    List<String> findDistinctValuesByProductAndAttribute(@Param("productId") UUID productId, 
                                                        @Param("attributeId") UUID attributeId);
    
    /**
     * Trouve une variante par ses valeurs d'attributs
     * 
     * USAGE CRUCIAL : Quand client sélectionne "Taille: M, Couleur: Rouge"
     * → Trouver la variante correspondante pour afficher prix/stock
     * 
     * Cette requête est complexe car elle doit matcher TOUTES les valeurs
     * 
     * @param productId ID du produit
     * @param attributeValues Map des valeurs (attributeId → value)
     * @return UUID ID de la variante correspondante (si elle existe)
     */
    @Query("SELECT v.id FROM Variant v " +
           "WHERE v.product.id = :productId " +
           "AND (SELECT COUNT(av) FROM AttributeValue av " +
           "     WHERE av.variant.id = v.id " +
           "     AND av.attribute.id IN :attributeIds " +
           "     AND av.value IN :values) = :expectedCount")
    List<UUID> findVariantByAttributeValues(@Param("productId") UUID productId,
                                          @Param("attributeIds") List<UUID> attributeIds,
                                          @Param("values") List<String> values,
                                          @Param("expectedCount") long expectedCount);
    
    /**
     * Trouve toutes les valeurs d'attributs pour un produit (groupées)
     * 
     * USAGE : Construire le sélecteur d'options sur la page produit
     * Résultat structuré : {Taille: [S,M,L], Couleur: [Rouge,Bleu]}
     * 
     * @param productId ID du produit
     * @return List<Object[]> [attributeName, attributeValue, attributeId]
     */
    @Query("SELECT a.name, av.value, a.id FROM AttributeValue av " +
           "JOIN Attribute a ON av.attribute.id = a.id " +
           "JOIN Variant v ON av.variant.id = v.id " +
           "WHERE v.product.id = :productId " +
           "ORDER BY a.name, av.value")
    List<Object[]> findAttributeValuesGroupedByProduct(@Param("productId") UUID productId);
    
    /**
     * Supprime toutes les valeurs d'attributs d'une variante
     * 
     * USAGE : Nettoyage lors de suppression de variante
     * 
     * @param variantId ID de la variante
     */
    @Query("DELETE FROM AttributeValue av WHERE av.variant.id = :variantId")
    void deleteByVariantId(@Param("variantId") UUID variantId);
    
    /**
     * Compte le nombre de valeurs d'attributs pour une variante
     * 
     * USAGE : Validation (une variante doit avoir au moins 1 attribut)
     * 
     * @param variantId ID de la variante
     * @return long nombre de valeurs d'attributs
     */
    long countByVariantId(UUID variantId);
    
    /**
     * Trouve les variantes ayant une valeur d'attribut spécifique
     * 
     * USAGE : Filtrer les produits par attribut
     * Ex: "Montrer tous les produits de couleur Rouge"
     * 
     * @param attributeId ID de l'attribut
     * @param value valeur recherchée
     * @return List<UUID> IDs des variantes correspondantes
     */
    @Query("SELECT av.variant.id FROM AttributeValue av " +
           "WHERE av.attribute.id = :attributeId AND av.value = :value")
    List<UUID> findVariantIdsByAttributeValue(@Param("attributeId") UUID attributeId, 
                                            @Param("value") String value);
    
    /**
     * Vérifie si une combinaison attribut-valeur existe pour un produit
     * 
     * USAGE : Validation avant ajout de variante
     * 
     * @param productId ID du produit
     * @param attributeId ID de l'attribut
     * @param value valeur de l'attribut
     * @return boolean true si cette combinaison existe
     */
    @Query("SELECT COUNT(av) > 0 FROM AttributeValue av " +
           "JOIN Variant v ON av.variant.id = v.id " +
           "WHERE v.product.id = :productId " +
           "AND av.attribute.id = :attributeId " +
           "AND av.value = :value")
    boolean existsByProductAndAttributeAndValue(@Param("productId") UUID productId,
                                              @Param("attributeId") UUID attributeId,
                                              @Param("value") String value);
}