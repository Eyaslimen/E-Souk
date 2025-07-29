package com.example.e_souk.Service;

import com.example.e_souk.Config.JwtTokenProvider;
import com.example.e_souk.Dto.AuthResponseDTO;
import com.example.e_souk.Dto.LoginRequestDTO;
import com.example.e_souk.Dto.RegisterRequestDTO;
import com.example.e_souk.Dto.UserProfileDTO;
import com.example.e_souk.Exception.AuthException;
import com.example.e_souk.Model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
        
        // Création de l'utilisateur
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword()) // Sera hashé dans le service
                .phone(registerRequest.getPhone())
                .address(registerRequest.getAddress())
                .codePostal(registerRequest.getCodePostal())
                .role(registerRequest.getRole())
                .build();
        
        User savedUser = userService.createUser(user);
        
        // Génération du token JWT
        String token = jwtTokenProvider.generateTokenFromUsername(savedUser.getUsername());
        
        // Création de la réponse
        AuthResponseDTO response = AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .picture(savedUser.getPicture())
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24)) // 24h
                .message("Inscription réussie")
                .build();
        
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
            
            // Création de la réponse
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .picture(user.getPicture())
                    .issuedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusHours(24)) // 24h
                    .message("Connexion réussie")
                    .build();
            
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
        
        return userService.toUserProfileDTO(user);
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