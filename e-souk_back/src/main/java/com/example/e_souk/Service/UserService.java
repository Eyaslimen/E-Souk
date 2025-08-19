package com.example.e_souk.Service;

import com.example.e_souk.Dto.User.UserProfileDTO;
import com.example.e_souk.Exception.AuthException;
import com.example.e_souk.Mappers.UserMapper;
import com.example.e_souk.Model.User;
import com.example.e_souk.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service pour la gestion des utilisateurs
 * Contient la logique métier pour les opérations sur les utilisateurs
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Crée un nouvel utilisateur
     * @param user Utilisateur à créer
     * @return Utilisateur créé avec ID généré
     */
    public User createUser(User user) {
        log.info("Création d'un nouvel utilisateur: {}", user.getUsername());
        
        // Vérification que le nom d'utilisateur n'existe pas déjà
        if (userRepository.existsByUsername(user.getUsername())) {
            throw AuthException.usernameAlreadyExists(user.getUsername());
        }
        
        // Vérification que l'email n'existe pas déjà
        if (userRepository.existsByEmail(user.getEmail())) {
            throw AuthException.emailAlreadyExists(user.getEmail());
        }
        
        // Hashage du mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Initialisation des valeurs par défaut
        user.setIsActive(true);
        user.setJoinedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        log.info("Utilisateur créé avec succès: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    /**
     * Trouve un utilisateur par son ID
     * @param userId ID de l'utilisateur
     * @return Utilisateur trouvé
     * @throws AuthException si l'utilisateur n'existe pas
     */
    public User findById(UUID userId) {
        log.debug("Recherche d'utilisateur par ID: {}", userId);
        
        return userRepository.findById(userId)
                .orElseThrow(() -> AuthException.userNotFound(userId.toString()));
    }
       /**
     * Trouve un utilisateur par son ID
     * @param userId ID de l'utilisateur
     * @return Utilisateur supprimer
     * @throws AuthException si l'utilisateur n'existe pas
     */
    public boolean deleteById(UUID userId) {
        log.debug("Suppression de l'utilisateur par ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            return false;
        }
        userRepository.deleteById(userId);
        return true;
    }
    /**
     * Trouve un utilisateur par son nom d'utilisateur
     * @param username Nom d'utilisateur
     * @return Utilisateur trouvé
     * @throws AuthException si l'utilisateur n'existe pas
     */
    public User findByUsername(String username) {
        log.debug("Recherche d'utilisateur par nom d'utilisateur: {}", username);
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> AuthException.userNotFound(username));
    }
    
    /**
     * Trouve un utilisateur par son email
     * @param email Email de l'utilisateur
     * @return Utilisateur trouvé
     * @throws AuthException si l'utilisateur n'existe pas
     */
    public User findByEmail(String email) {
        log.debug("Recherche d'utilisateur par email: {}", email);
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> AuthException.userNotFound(email));
    }
    
    /**
     * Trouve un utilisateur par nom d'utilisateur ou email
     * @param usernameOrEmail Nom d'utilisateur ou email
     * @return Utilisateur trouvé
     * @throws AuthException si l'utilisateur n'existe pas
     */
    public User findByUsernameOrEmail(String usernameOrEmail) {
        log.debug("Recherche d'utilisateur par nom d'utilisateur ou email: {}", usernameOrEmail);
        
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> AuthException.userNotFound(usernameOrEmail));
    }
    
    /**
     * Met à jour un utilisateur existant à partir du UserProfileDTO
     * @param userDTO Données du profil utilisateur à mettre à jour
     * @return Utilisateur mis à jour
     */
    public User updateUser(UserProfileDTO userDTO) {
        // Récupérer l'utilisateur existant
        User user = findById(userDTO.getId());

        // Mettre à jour les champs modifiables
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPicture(userDTO.getPicture());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setCodePostal(userDTO.getCodePostal());
        user.setRole(userDTO.getRole());
        user.setIsActive(userDTO.getIsActive());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Utilisateur mis à jour avec succès: {}", updatedUser.getUsername());
        return updatedUser;
    }
    
    /**
     * Désactive un utilisateur
     * @param userId ID de l'utilisateur à désactiver
     * @return Utilisateur désactivé
     */
    public User deactivateUser(UUID userId) {
        log.info("Désactivation de l'utilisateur: {}", userId);
        
        User user = findById(userId);
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        
        User deactivatedUser = userRepository.save(user);
        log.info("Utilisateur désactivé avec succès: {}", deactivatedUser.getUsername());
        
        return deactivatedUser;
    }
    
    /**
     * Réactive un utilisateur
     * @param userId ID de l'utilisateur à réactiver
     * @return Utilisateur réactivé
     */
    public User reactivateUser(UUID userId) {
        log.info("Réactivation de l'utilisateur: {}", userId);
        
        User user = findById(userId);
        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        User reactivatedUser = userRepository.save(user);
        log.info("Utilisateur réactivé avec succès: {}", reactivatedUser.getUsername());
        
        return reactivatedUser;
    }
    
    /**
     * Change le mot de passe d'un utilisateur
     * @param userId ID de l'utilisateur
     * @param newPassword Nouveau mot de passe
     * @return Utilisateur avec le nouveau mot de passe
     */
    public User changePassword(UUID userId,String oldPassword, String newPassword) {
        log.info("Changement de mot de passe pour l'utilisateur: {}", userId);
        
        User user = findById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthException("Ancien mot de passe incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        log.info("Mot de passe changé avec succès pour l'utilisateur: {}", updatedUser.getUsername());
        
        return updatedUser;
    }
    
    /**
     * Vérifie si un nom d'utilisateur existe
     * @param username Nom d'utilisateur à vérifier
     * @return true si le nom d'utilisateur existe
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Vérifie si un email existe
     * @param email Email à vérifier
     * @return true si l'email existe
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Trouve tous les utilisateurs actifs
     * @return Liste des utilisateurs actifs
     */
    public List<User> findAllActive() {
        return userRepository.findAllActive();
    }
    /**
     * Trouve tous les utilisateurs 
     * @return Liste des utilisateurs
     */
    public List<UserProfileDTO> findAll() {
        List<User> users = userRepository.findAll();
        List<UserProfileDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(UserMapper.toUserProfileDTO(user));
        }
        return userDTOs;
    }
    
} 