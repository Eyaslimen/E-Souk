package com.example.e_souk.Repository;

import com.example.e_souk.Model.Role;
import com.example.e_souk.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour l'entité User
 * Fournit les méthodes d'accès aux données utilisateur
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Trouve un utilisateur par son nom d'utilisateur
     * @param username Nom d'utilisateur à rechercher
     * @return Optional contenant l'utilisateur s'il existe
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Trouve un utilisateur par son email
     * @param email Email à rechercher
     * @return Optional contenant l'utilisateur s'il existe
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Vérifie si un nom d'utilisateur existe déjà
     * @param username Nom d'utilisateur à vérifier
     * @return true si le nom d'utilisateur existe
     */
    boolean existsByUsername(String username);
    
    /**
     * Vérifie si un email existe déjà
     * @param email Email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmail(String email);
    
    /**
     * Trouve un utilisateur par nom d'utilisateur ou email
     * @param usernameOrEmail Nom d'utilisateur ou email
     * @return Optional contenant l'utilisateur s'il existe
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    /**
     * Trouve tous les utilisateurs actifs
     * @return Liste des utilisateurs actifs
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActive();
    
    /**
     * Trouve tous les utilisateurs par rôle
     * @param role Rôle à rechercher
     * @return Liste des utilisateurs avec ce rôle
     */
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") Role role);
    
    /**
     * Trouve tous les utilisateurs actifs par rôle
     * @param role Rôle à rechercher
     * @return Liste des utilisateurs actifs avec ce rôle
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true")
    List<User> findActiveByRole(@Param("role") Role role);
    
    /**
     * Compte le nombre d'utilisateurs par rôle
     * @param role Rôle à compter
     * @return Nombre d'utilisateurs avec ce rôle
     */
    long countByRole(Role role);
    
    /**
     * Compte le nombre d'utilisateurs actifs par rôle
     * @param role Rôle à compter
     * @return Nombre d'utilisateurs actifs avec ce rôle
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveByRole(@Param("role") Role role);
} 