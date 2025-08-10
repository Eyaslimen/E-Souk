// utiliser UserService içi 

package com.example.e_souk.Controller;

import com.example.e_souk.Dto.ChangePasswordDTO;

import com.example.e_souk.Dto.UserProfileDTO;
import com.example.e_souk.Model.User;
import com.example.e_souk.Service.AuthService;
import com.example.e_souk.Service.UserService;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Utilisateurs", description = "Endpoints pour la gestion des utilisateurs")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Modifier un utilisateur
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/update")
    @Operation(
        summary = "Mise à jour du profil utilisateur",
        description = "Met à jour les informations du profil utilisateur"
    )
    public ResponseEntity<User> updateUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO) {
        // Utiliser userService pour mettre à jour le profil utilisateur
        User updatedProfile = userService.updateUser(userProfileDTO);
        return ResponseEntity.ok(updatedProfile);
    }
     /**
     * Modifier le mot de passe 
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/updatePassword/{id}")
    @Operation(
        summary = "Mise à jour du mot de passe",
        description = "Met à jour le mot de passe de l'utilisateur"
    )
    public ResponseEntity<User> changePassword(@PathVariable("id") UUID id,@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        User updatedUser = userService.changePassword(id, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
        return ResponseEntity.ok(updatedUser);
    }
    /**
     * Récupère tous les utilisateurs
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    @Operation(
        summary = "recuperation de tous les utilisateurs",
        description = "Récupère tous les utilisateurs"
    )
    public ResponseEntity<List<UserProfileDTO>> getAll() {
        List<UserProfileDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    /**
     * Récupérer un utilisateur par son ID
     */
    
    /**
     * Supprimer un utilisateur
     */
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur non trouvé ou suppression échouée."));
        }
    }


}