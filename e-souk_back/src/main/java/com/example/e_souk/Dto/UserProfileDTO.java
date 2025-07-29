package com.example.e_souk.Dto;

import com.example.e_souk.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour le profil utilisateur
 * Contient les informations publiques de l'utilisateur (sans mot de passe)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    
    /**
     * ID unique de l'utilisateur
     */
    private UUID id;
    
    /**
     * Nom d'utilisateur unique
     */
    private String username;
    
    /**
     * Email de l'utilisateur
     */
    private String email;
    
    /**
     * Photo de profil (optionnelle)
     */
    private String picture;
    
    /**
     * Numéro de téléphone (optionnel)
     */
    private String phone;
    
    /**
     * Adresse postale (optionnelle)
     */
    private String address;
    
    /**
     * Code postal (optionnel)
     */
    private String codePostal;
    
    /**
     * Rôle de l'utilisateur
     */
    private Role role;
    
    /**
     * Indique si le compte est actif
     */
    private Boolean isActive;
    
    /**
     * Date de création du compte
     */
    private LocalDateTime joinedAt;
    
    /**
     * Date de dernière modification du profil
     */
    private LocalDateTime updatedAt;
} 