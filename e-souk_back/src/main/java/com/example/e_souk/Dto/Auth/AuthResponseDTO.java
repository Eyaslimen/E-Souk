package com.example.e_souk.Dto.Auth;

import com.example.e_souk.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour les réponses d'authentification
 * Contient le token JWT et les informations de l'utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    /**    
     * Token JWT pour l'authentification
     */
    private String token;
    
    /**
     * Type de token (Bearer)
     */
    private String type = "Bearer";
    
    /**
     * ID unique de l'utilisateur
     */
    private UUID id;
    
    /**
     * Nom d'utilisateur
     */
    private String username;
    
    /**
     * Email de l'utilisateur
     */
    private String email;
    
    /**
     * Rôle de l'utilisateur
     */
    private Role role;
    
    /**
     * Photo de profil (optionnelle)
     */
    private String picture;
    
    /**
     * Date de création du token
     */
    private LocalDateTime issuedAt;
    
    /**
     * Date d'expiration du token
     */
    private LocalDateTime expiresAt;
    
    /**
     * Message de succès
     */
    private String message;
} 