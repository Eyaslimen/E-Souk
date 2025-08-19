package com.example.e_souk.Service;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_souk.Config.JwtTokenProvider;
import com.example.e_souk.Dto.Auth.AuthResponseDTO;
import com.example.e_souk.Dto.Auth.LoginRequestDTO;
import com.example.e_souk.Dto.Auth.RegisterRequestDTO;
import com.example.e_souk.Dto.User.UserProfileDTO;
import com.example.e_souk.Exception.AuthException;
import com.example.e_souk.Mappers.AuthMapper;
import com.example.e_souk.Mappers.UserMapper;
import com.example.e_souk.Model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service d'authentification
 * Gère l'inscription, la connexion et la génération des tokens JWT
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final FileStorageService fileStorageService; // Add dependency
/**
     * Inscription d'un nouvel utilisateur
     * @param registerRequest DTO contenant les données d'inscription
     * @return Réponse d'authentification avec token JWT
     */
    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        log.info("Tentative d'inscription pour l'utilisateur: {}", registerRequest.getUsername());
        // Vérification que le nom d'utilisateur n'existe pas déjà
        if (userService.usernameExists(registerRequest.getUsername())) {
            throw AuthException.usernameAlreadyExists(registerRequest.getUsername());
        }
        // Vérification que l'email n'existe pas déjà
        if (userService.emailExists(registerRequest.getEmail())) {
            throw AuthException.emailAlreadyExists(registerRequest.getEmail());
        }
        // Gérer l'upload de la photo de profil
        String savedFileName = null;
        MultipartFile profilePicture = registerRequest.getProfilePicture();
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                savedFileName = fileStorageService.storeFile(profilePicture);
                log.info("Photo de profil enregistrée pour l'utilisateur {}: {}", 
                        registerRequest.getUsername(), savedFileName);
            } catch (IOException e) {
                log.error("Erreur lors de l'enregistrement de la photo de profil pour l'utilisateur {}", 
                        registerRequest.getUsername(), e);
                throw new RuntimeException("Erreur lors de l'enregistrement de la photo de profil.");
            } catch (IllegalArgumentException e) {
                log.warn("Type de fichier invalide pour l'utilisateur {}: {}", 
                        registerRequest.getUsername(), e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        // Création de l'utilisateur
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword()) // Encoder le mot de passe
                .phone(registerRequest.getPhone())
                .address(registerRequest.getAddress())
                .codePostal(registerRequest.getCodePostal())
                .role(registerRequest.getRole())
                .picture(savedFileName) // Enregistrer le chemin de l'image
                .build();
        
        User savedUser = userService.createUser(user);
        // Génération du token JWT
        String token = jwtTokenProvider.generateTokenFromUsername(savedUser.getUsername());
        
        AuthResponseDTO response = AuthMapper.toAuthResponseDTO(savedUser, token, "Inscription réussie");
        
        log.info("Inscription réussie pour l'utilisateur: {}", savedUser.getUsername());
        return response;
    }

    /**
     * Connexion d'un utilisateur
     * @param loginRequest DTO contenant les identifiants de connexion
     * @return Réponse d'authentification avec token JWT
     */

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Tentative de connexion pour: {}", loginRequest.getUsernameOrEmail());
        try {
            // Authentification avec Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
            // Stockage de l'authentification dans le contexte
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Génération du token JWT
            String token = jwtTokenProvider.generateToken(authentication);
            // Récupération des informations utilisateur
            User user = userService.findByUsernameOrEmail(loginRequest.getUsernameOrEmail());
            // Vérification que le compte est actif
            if (!user.getIsActive()) {
                throw AuthException.accountInactive();
            }
            AuthResponseDTO response = AuthMapper.toAuthResponseDTO(user, token, "Connexion réussie");
            log.info("Connexion réussie pour l'utilisateur: {}", user.getUsername());
            return response;
        } catch (Exception e) {
            log.warn("Échec de connexion pour: {}", loginRequest.getUsernameOrEmail());
            throw AuthException.invalidCredentials();
        }
    }
    /**
     * Récupère le profil de l'utilisateur authentifié
     * @return Profil utilisateur
     */
    public UserProfileDTO getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw AuthException.invalidToken();
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
    return UserMapper.toUserProfileDTO(user);
    }
    /**
     * Récupère l'utilisateur authentifié
     * @return Utilisateur authentifié
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw AuthException.invalidToken();
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
    return user;
    }
    /**
     * Valide un token JWT
     * @param token Token JWT à valider
     * @return true si le token est valide
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
    
    /**
     * Extrait le nom d'utilisateur depuis un token JWT
     * @param token Token JWT
     * @return Nom d'utilisateur
     */
    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
    /**
     * Vérifie si un token va expirer bientôt
     * @param token Token JWT
     * @return true si le token expire bientôt
     */
    public boolean isTokenExpiringSoon(String token) {
        return jwtTokenProvider.isTokenExpiringSoon(token);
    }
}