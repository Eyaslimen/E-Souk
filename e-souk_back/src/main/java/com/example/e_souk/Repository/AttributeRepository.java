package com.example.e_souk.Repository;

import com.example.e_souk.Model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour gérer les attributs (Taille, Couleur, etc.)
 * 
 * CONCEPT CLÉ : Les attributs sont réutilisables entre produits
 * Ex: L'attribut "Taille" peut être utilisé par plusieurs produits
 */
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {
    
    /**
     * Trouve un attribut par son nom (insensible à la casse)
     * 
     * USAGE : Réutiliser les attributs existants (éviter les doublons)
     * Ex: Si "Taille" existe déjà, on le réutilise
     * 
     * @param name nom de l'attribut
     * @return Optional<Attribute> l'attribut s'il existe
     */
    @Query("SELECT a FROM Attribute a WHERE LOWER(a.name) = LOWER(:name)")
    Optional<Attribute> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Recherche des attributs par nom partiel
     * 
     * USAGE : Auto-complétion lors de la création de produit
     * 
     * @param searchTerm terme de recherche
     * @return List<Attribute> attributs correspondants
     */
    @Query("SELECT a FROM Attribute a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Attribute> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    /**
     * Trouve tous les attributs utilisés par un produit
     * 
     * USAGE : Afficher les options d'un produit (Taille: S,M,L + Couleur: Rouge,Bleu)
     * 
     * @param productId ID du produit
     * @return List<Attribute> attributs de ce produit (via ses variantes)
     */
    @Query("SELECT DISTINCT a FROM Attribute a " +
           "JOIN AttributeValue av ON av.attribute.id = a.id " +
           "JOIN Variant v ON av.variant.id = v.id " +
           "WHERE v.product.id = :productId")
    List<Attribute> findAttributesByProductId(@Param("productId") UUID productId);
    
    /**
     * Compte combien de produits utilisent cet attribut
     * 
     * USAGE : Statistiques, savoir si on peut supprimer un attribut
     * 
     * @param attributeId ID de l'attribut
     * @return long nombre de produits utilisant cet attribut
     */
    @Query("SELECT COUNT(DISTINCT v.product.id) FROM Variant v " +
           "JOIN AttributeValue av ON av.variant.id = v.id " +
           "WHERE av.attribute.id = :attributeId")
    long countProductsUsingAttribute(@Param("attributeId") UUID attributeId);
    
    /**
     * Trouve les attributs les plus utilisés (statistiques)
     * 
     * USAGE : Dashboard admin, suggestions d'attributs populaires
     * 
     * @param limit nombre d'attributs à retourner
     * @return List<Attribute> attributs les plus populaires
     */
    @Query("SELECT a FROM Attribute a " +
           "ORDER BY (SELECT COUNT(DISTINCT v.product.id) FROM Variant v " +
           "JOIN AttributeValue av ON av.variant.id = v.id " +
           "WHERE av.attribute.id = a.id) DESC")
    List<Attribute> findMostUsedAttributes(org.springframework.data.domain.Pageable pageable);
    
    /**
     * Vérifie si un attribut est utilisé par des variantes
     * 
     * USAGE : Sécurité avant suppression
     * 
     * @param attributeId ID de l'attribut
     * @return boolean true si l'attribut est utilisé
     */
    @Query("SELECT COUNT(av) > 0 FROM AttributeValue av WHERE av.attribute.id = :attributeId")
    boolean isAttributeInUse(@Param("attributeId") UUID attributeId);
}