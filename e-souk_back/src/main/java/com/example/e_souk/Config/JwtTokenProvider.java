package com.example.e_souk.Config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Fournisseur de tokens JWT pour l'authentification
 * Gère la génération, validation et extraction des informations des tokens
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    
    private final JwtConfig jwtConfig;
    
    /**
     * Génère un token JWT pour un utilisateur authentifié
     * @param authentication L'authentification Spring Security
     * @return Token JWT généré
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userDetails.getUsername());
    }
    
    /**
     * Génère un token JWT à partir d'un nom d'utilisateur
     * @param username Nom d'utilisateur
     * @return Token JWT généré
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
        
        // Création de la clé secrète à partir de la chaîne de caractères
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }
    
    /**
     * Extrait le nom d'utilisateur depuis un token JWT
     * @param token Token JWT
     * @return Nom d'utilisateur
     */
    public String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.getSubject();
        } catch (JwtException e) {
            log.error("Erreur lors de l'extraction du nom d'utilisateur du token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Valide un token JWT
     * @param token Token JWT à valider
     * @return true si le token est valide, false sinon
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            
            return true;
        } catch (SecurityException e) {
            log.error("Signature JWT invalide: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformé: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expiré: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT non supporté: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Chaîne de claims JWT vide: {}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Extrait le token depuis l'en-tête Authorization
     * @param authHeader En-tête Authorization (ex: "Bearer token123")
     * @return Token extrait ou null si invalide
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(jwtConfig.getPrefix())) {
            return authHeader.substring(jwtConfig.getPrefix().length());
        }
        return null;
    }
    
    /**
     * Vérifie si un token va expirer bientôt (dans les 5 minutes)
     * @param token Token JWT
     * @return true si le token expire bientôt
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            // Vérifie si le token expire dans les 5 prochaines minutes
            long fiveMinutesInMs = 5 * 60 * 1000;
            return expiration.getTime() - now.getTime() < fiveMinutesInMs;
            
        } catch (JwtException e) {
            log.error("Erreur lors de la vérification d'expiration du token: {}", e.getMessage());
            return false;
        }
    }
} 