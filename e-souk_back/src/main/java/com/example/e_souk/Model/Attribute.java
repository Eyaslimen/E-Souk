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
 * Entité représentant un attribut de produit dans la marketplace
 * Définit les caractéristiques possibles pour les variantes (ex: Taille, Couleur, Matériau)
 */
@Entity
@Table(name = "attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Attribute {
    
    /**
     * Identifiant unique de l'attribut
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Nom de l'attribut (ex: "Taille", "Couleur", "Matériau")
     * Doit être unique et non vide
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le nom de l'attribut est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String name;
    
    /**
     * Type de l'attribut (ex: "TEXT", "NUMBER", "COLOR")
     * Permet de définir le format attendu pour les valeurs
     */
    @Column(name = "type", nullable = false, length = 20)
    @NotBlank(message = "Le type de l'attribut est obligatoire")
    @Size(max = 20, message = "Le type ne peut pas dépasser 20 caractères")
    private String type;
    
    /**
     * Indique si l'attribut est obligatoire pour toutes les variantes
     * Si true, toutes les variantes d'un produit doivent avoir une valeur pour cet attribut
     */
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;
    
    /**
     * Date de création de l'attribut
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
} 