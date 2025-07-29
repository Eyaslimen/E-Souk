package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
 * Entité représentant une catégorie de produits dans la marketplace
 * Permet d'organiser les produits par catégories (ex: Électronique, Vêtements, etc.)
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Category {
    
    /**
     * Identifiant unique de la catégorie
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Nom de la catégorie (ex: "Électronique", "Vêtements")
     * Doit être unique et non vide
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;
    
    /**
     * Description détaillée de la catégorie
     * Permet d'expliquer ce que contient cette catégorie
     */
    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    /**
     * Indique si la catégorie est active et visible
     * Permet de désactiver temporairement une catégorie sans la supprimer
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Date de création de la catégorie
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 