/**
 * This Java class is an authentication controller that handles user registration, login, profile
 * retrieval, and JWT token validation with Swagger documentation.
 */
package com.example.e_souk.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_souk.Dto.Auth.AuthResponseDTO;
import com.example.e_souk.Dto.Auth.LoginRequestDTO;
import com.example.e_souk.Dto.Auth.RegisterRequestDTO;
import com.example.e_souk.Service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// @ApiResponses : ça est du swagger doc , peut etre utilisée pour documenter les réponses de l'API
/**
 * Controller pour l'authentification
 * Gère l'inscription, la connexion et la récupération du profil utilisateur
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "Endpoints pour l'inscription, connexion et gestion du profil utilisateur")
public class AuthController {
    
    private final AuthService authService;
    /**
     * Inscription d'un nouvel utilisateur
     * @param registerRequest DTO contenant les données d'inscription
     * @return Réponse avec token JWT et informations utilisateur
     */
        @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)    
        @Operation(
        summary = "Inscription d'un nouvel utilisateur",
        description = "Crée un nouveau compte utilisateur et retourne un token JWT pour l'authentification"
    )
    public ResponseEntity<AuthResponseDTO> register(
            @ModelAttribute RegisterRequestDTO registerRequest) {
        
        log.info("Requête d'inscription reçue pour: {}", registerRequest.getUsername());
        
        AuthResponseDTO response = authService.register(registerRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    
    /**
     * Connexion d'un utilisateur
     * @param loginRequest DTO contenant les identifiants de connexion
     * @return Réponse avec token JWT et informations utilisateur
     */
    @PostMapping("/login")
    @Operation(
        summary = "Connexion d'un utilisateur",
        description = "Authentifie un utilisateur avec ses identifiants et retourne un token JWT"
    )
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        log.info("Requête de connexion reçue pour: {}", loginRequest.getUsernameOrEmail());
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
   
    
    /**
     * Validation d'un token JWT
     * @param token Token JWT à valider
     * @return true si le token est valide
     */
    @PostMapping("/validate")
    @Operation(
        summary = "Validation d'un token JWT",
        description = "Vérifie si un token JWT est valide et non expiré"
    )
    public ResponseEntity<Object> validateToken(@RequestParam String token) {
        
        log.debug("Requête de validation de token");
        
        boolean isValid = authService.validateToken(token);
        
        if (isValid) {
            return ResponseEntity.ok(Map.of("valid", true, "message", "Token valide"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Token invalide ou expiré"));
        }
    }
} 
