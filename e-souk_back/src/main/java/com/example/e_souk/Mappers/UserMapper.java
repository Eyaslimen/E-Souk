package com.example.e_souk.Mappers;

import com.example.e_souk.Model.User;
import com.example.e_souk.Dto.UserProfileDTO;

public class UserMapper {
    /**
     * Convertit un User en UserProfileDTO
     * @param user Utilisateur à convertir
     * @return UserProfileDTO correspondant
     */
    public static UserProfileDTO toUserProfileDTO(User user) {
        if (user == null) return null;
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .picture(user.getPicture())
                .phone(user.getPhone())
                .address(user.getAddress())
                .codePostal(user.getCodePostal())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .joinedAt(user.getJoinedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    // Convertit un UserProfileDTO en User
    public static User toUser(UserProfileDTO userProfileDTO) {
        if (userProfileDTO == null) return null;

        return User.builder()
                .id(userProfileDTO.getId())
                .username(userProfileDTO.getUsername())
                .email(userProfileDTO.getEmail())
                .picture(userProfileDTO.getPicture())
                .phone(userProfileDTO.getPhone())
                .address(userProfileDTO.getAddress())
                .codePostal(userProfileDTO.getCodePostal())
                .role(userProfileDTO.getRole())
                .isActive(userProfileDTO.getIsActive())
                .joinedAt(userProfileDTO.getJoinedAt())
                .updatedAt(userProfileDTO.getUpdatedAt())
                .build();
    }

}
