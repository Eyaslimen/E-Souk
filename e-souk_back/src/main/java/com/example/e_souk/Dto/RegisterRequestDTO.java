package com.example.e_souk.Dto;

import com.example.e_souk.Model.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les requêtes d'inscription d'utilisateur
 * Contient toutes les données nécessaires pour créer un nouveau compte
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    
    /**
     * Nom d'utilisateur unique
     * Doit être entre 3 et 50 caractères
     */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Le nom d'utilisateur ne peut contenir que des lettres, chiffres et underscores")
    private String username;
    
    /**
     * Adresse email unique
     * Doit être un email valide
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;
    
    /**
     * Mot de passe
     * Doit être entre 6 et 100 caractères
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;
    
    /**
     * Numéro de téléphone (optionnel)
     * Doit être un numéro valide si fourni
     */
    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Le numéro de téléphone doit être valide")
    private String phone;
    
    /**
     * Adresse postale (optionnelle)
     */
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String address;
    
    /**
     * Code postal (optionnel)
     * Doit être un code postal valide si fourni
     */
    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    @Pattern(regexp = "^[0-9A-Z\\s-]+$", message = "Le code postal doit être valide")
    private String codePostal;
    
    /**
     * Rôle de l'utilisateur
     * Par défaut CLIENT si non spécifié
     */
    @NotNull(message = "Le rôle est obligatoire")
    private Role role = Role.CLIENT;
} 