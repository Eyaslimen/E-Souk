package com.example.e_souk.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.e_souk.Model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour gérer les opérations CRUD sur les catégories
 * 
 * POURQUOI JpaRepository<Category, UUID> ?
 * - JpaRepository nous donne automatiquement les méthodes : save(), findById(), findAll(), delete()
 * - Category : l'entité qu'on manipule
 * - UUID : le type de la clé primaire (id)
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    /**
     * Recherche une catégorie par son nom (insensible à la casse)
     * 
     * POURQUOI cette méthode ?
     * - Éviter les doublons de catégories
     * - Recherche intelligente pour l'utilisateur
     * 
     * @param name nom de la catégorie
     * @return Optional<Category> - peut être vide si pas trouvé
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Recherche les catégories dont le nom contient le texte recherché
     * 
     * POURQUOI cette méthode ?
     * - Pour l'auto-complétion côté frontend
     * - Recherche partielle pour une meilleure UX
     * 
     * @param searchTerm terme de recherche
     * @return List<Category> - liste des catégories correspondantes
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Category> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    /**
     * Compte le nombre de produits dans une catégorie
     * 
     * POURQUOI cette méthode ?
     * - Savoir si on peut supprimer une catégorie (si elle a des produits)
     * - Statistiques pour l'admin
     * 
     * @param categoryId ID de la catégorie
     * @return long - nombre de produits
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countProductsInCategory(@Param("categoryId") UUID categoryId);
}