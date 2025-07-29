package com.example.e_souk.Exception;

/**
 * Exception personnalisée pour les erreurs d'authentification
 * Utilisée pour gérer les erreurs spécifiques à l'auth (connexion, inscription, etc.)
 */
public class AuthException extends RuntimeException {
    
    /**
     * Constructeur avec message d'erreur
     * @param message Message d'erreur
     */
    public AuthException(String message) {
        super(message);
    }
    
    /**
     * Constructeur avec message et cause
     * @param message Message d'erreur
     * @param cause Cause de l'exception
     */
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Exception pour nom d'utilisateur déjà utilisé
     * @param username Nom d'utilisateur en conflit
     * @return AuthException
     */
    public static AuthException usernameAlreadyExists(String username) {
        return new AuthException("Le nom d'utilisateur '" + username + "' est déjà utilisé");
    }
    
    /**
     * Exception pour email déjà utilisé
     * @param email Email en conflit
     * @return AuthException
     */
    public static AuthException emailAlreadyExists(String email) {
        return new AuthException("L'email '" + email + "' est déjà utilisé");
    }
    
    /**
     * Exception pour identifiants invalides
     * @return AuthException
     */
    public static AuthException invalidCredentials() {
        return new AuthException("Nom d'utilisateur/email ou mot de passe incorrect");
    }
    
    /**
     * Exception pour compte inactif
     * @return AuthException
     */
    public static AuthException accountInactive() {
        return new AuthException("Ce compte est désactivé");
    }
    
    /**
     * Exception pour token invalide
     * @return AuthException
     */
    public static AuthException invalidToken() {
        return new AuthException("Token d'authentification invalide ou expiré");
    }
    
    /**
     * Exception pour utilisateur non trouvé
     * @param identifier Identifiant (username, email, ou ID)
     * @return AuthException
     */
    public static AuthException userNotFound(String identifier) {
        return new AuthException("Utilisateur non trouvé: " + identifier);
    }
} 