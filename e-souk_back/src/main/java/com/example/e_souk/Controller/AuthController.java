package com.example.e_souk.Controller;

import com.example.e_souk.Dto.AuthResponseDTO;
import com.example.e_souk.Dto.LoginRequestDTO;
import com.example.e_souk.Dto.RegisterRequestDTO;
import com.example.e_souk.Dto.UserProfileDTO;
import com.example.e_souk.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @PostMapping("/register")
    @Operation(
        summary = "Inscription d'un nouvel utilisateur",
        description = "Crée un nouveau compte utilisateur et retourne un token JWT pour l'authentification"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Inscription réussie",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class),
                examples = @ExampleObject(
                    name = "Inscription réussie",
                    value = """
                    {
                        "token": "eyJhbGciOiJIUzUxMiJ9...",
                        "type": "Bearer",
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "username": "john_doe",
                        "email": "john@example.com",
                        "role": "CLIENT",
                        "picture": null,
                        "issuedAt": "2024-01-15T10:30:00",
                        "expiresAt": "2024-01-16T10:30:00",
                        "message": "Inscription réussie"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides ou utilisateur déjà existant",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Erreur de validation",
                    value = """
                    {
                        "timestamp": "2024-01-15T10:30:00",
                        "status": 400,
                        "error": "Erreur de validation",
                        "message": "Les données fournies sont invalides",
                        "details": {
                            "username": "Le nom d'utilisateur est obligatoire",
                            "email": "L'email doit être valide"
                        }
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO registerRequest) {
        
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
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Connexion réussie",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class),
                examples = @ExampleObject(
                    name = "Connexion réussie",
                    value = """
                    {
                        "token": "eyJhbGciOiJIUzUxMiJ9...",
                        "type": "Bearer",
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "username": "john_doe",
                        "email": "john@example.com",
                        "role": "CLIENT",
                        "picture": null,
                        "issuedAt": "2024-01-15T10:30:00",
                        "expiresAt": "2024-01-16T10:30:00",
                        "message": "Connexion réussie"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Identifiants invalides",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Identifiants invalides",
                    value = """
                    {
                        "timestamp": "2024-01-15T10:30:00",
                        "status": 401,
                        "error": "Identifiants invalides",
                        "message": "Nom d'utilisateur/email ou mot de passe incorrect"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        
        log.info("Requête de connexion reçue pour: {}", loginRequest.getUsernameOrEmail());
        
        AuthResponseDTO response = authService.login(loginRequest);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupération du profil de l'utilisateur authentifié
     * @return Profil utilisateur
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Récupération du profil utilisateur",
        description = "Récupère les informations du profil de l'utilisateur authentifié"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profil récupéré avec succès",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileDTO.class),
                examples = @ExampleObject(
                    name = "Profil utilisateur",
                    value = """
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "username": "john_doe",
                        "email": "john@example.com",
                        "picture": null,
                        "phone": "+33123456789",
                        "address": "123 Rue de la Paix",
                        "codePostal": "75001",
                        "role": "CLIENT",
                        "isActive": true,
                        "joinedAt": "2024-01-01T10:00:00",
                        "updatedAt": "2024-01-15T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non authentifié",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Non authentifié",
                    value = """
                    {
                        "timestamp": "2024-01-15T10:30:00",
                        "status": 401,
                        "error": "Non authentifié",
                        "message": "Token d'authentification requis"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<UserProfileDTO> getProfile() {
        
        log.debug("Requête de récupération de profil");
        
        UserProfileDTO profile = authService.getCurrentUserProfile();
        
        return ResponseEntity.ok(profile);
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
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token valide",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Token valide",
                    value = """
                    {
                        "valid": true,
                        "message": "Token valide"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Token invalide",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Token invalide",
                    value = """
                    {
                        "valid": false,
                        "message": "Token invalide ou expiré"
                    }
                    """
                )
            )
        )
    })
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