package com.example.e_souk.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les requêtes de connexion
 * Contient les identifiants de connexion
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
    
    /**
     * Nom d'utilisateur ou email
     * L'utilisateur peut se connecter avec son nom d'utilisateur ou son email
     */
    @NotBlank(message = "Le nom d'utilisateur ou l'email est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom d'utilisateur ou l'email doit contenir entre 3 et 100 caractères")
    private String usernameOrEmail;
    
    /**
     * Mot de passe
     * Doit être fourni pour l'authentification
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 1, max = 100, message = "Le mot de passe ne peut pas être vide")
    private String password;
} 