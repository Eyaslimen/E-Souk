package com.example.e_souk.Service;

import com.example.e_souk.Config.UserDetailsImpl;
import com.example.e_souk.Model.User;
import com.example.e_souk.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service UserDetails pour Spring Security
 * Charge les informations utilisateur depuis la base de données
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    /**
     * Charge un utilisateur par son nom d'utilisateur
     * @param username Nom d'utilisateur à rechercher
     * @return UserDetails de l'utilisateur
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Tentative de chargement de l'utilisateur: {}", username);
        
        // Recherche de l'utilisateur par nom d'utilisateur
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé: {}", username);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + username);
                });
        
        // Vérification que le compte est actif
        if (!user.getIsActive()) {
            log.warn("Tentative de connexion avec un compte inactif: {}", username);
            throw new UsernameNotFoundException("Compte inactif: " + username);
        }
        
        log.debug("Utilisateur chargé avec succès: {}", username);
        return new UserDetailsImpl(user);
    }
    
    /**
     * Charge un utilisateur par son ID
     * @param userId ID de l'utilisateur
     * @return UserDetails de l'utilisateur
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé
     */
    @Transactional
    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        log.debug("Tentative de chargement de l'utilisateur par ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé par ID: {}", userId);
                    return new UsernameNotFoundException("Utilisateur non trouvé par ID: " + userId);
                });
        
        if (!user.getIsActive()) {
            log.warn("Tentative d'accès à un compte inactif par ID: {}", userId);
            throw new UsernameNotFoundException("Compte inactif par ID: " + userId);
        }
        
        log.debug("Utilisateur chargé avec succès par ID: {}", userId);
        return new UserDetailsImpl(user);
    }
} 