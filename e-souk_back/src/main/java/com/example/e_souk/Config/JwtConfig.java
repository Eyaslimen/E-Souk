package com.example.e_souk.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration JWT pour la gestion des tokens d'authentification
 * Récupère les propriétés depuis application.properties
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    
    /**
     * Clé secrète pour signer et valider les tokens JWT
     * Doit être suffisamment longue et complexe en production
     */
    private String secret;
    
    /**
     * Durée d'expiration du token en millisecondes
     * Par défaut : 24 heures (86400000 ms)
     */
    private Long expiration = 86400000L;
    
    /**
     * Type de token (Bearer)
     */
    private String type = "Bearer";
    
    /**
     * En-tête HTTP pour le token
     */
    private String header = "Authorization";
    
    /**
     * Préfixe du token dans l'en-tête
     */
    private String prefix = "Bearer ";
} 