package com.example.e_souk.Dto.User;

import com.example.e_souk.Model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour le profil utilisateur
 * Contient les informations publiques de l'utilisateur (sans mot de passe)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    
    private UUID id;

    private String username;

    private String email;

    private String picture;

    private String phone;

    private String address;
    

    private String codePostal;
    
    private Role role;
    

    private Boolean isActive;
    

    private LocalDateTime joinedAt;
    
    private LocalDateTime updatedAt;
} 