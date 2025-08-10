package com.example.e_souk.Mappers;

import com.example.e_souk.Model.User;
import com.example.e_souk.Dto.AuthResponseDTO;
import java.time.LocalDateTime;

public class AuthMapper {
    public static AuthResponseDTO toAuthResponseDTO(User user, String token, String message) {
        if (user == null) return null;
        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .picture(user.getPicture())
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .message(message)
                .build();
    }
}
