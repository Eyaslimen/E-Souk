package com.example.e_souk.Config;

import com.example.e_souk.Model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

/**
 * Implémentation de UserDetails pour Spring Security
 * Adapte notre entité User au système d'authentification de Spring Security
 */
@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * ID unique de l'utilisateur
     */
    private UUID id;
    
    /**
     * Nom d'utilisateur (unique)
     */
    private String username;
    
    /**
     * Email de l'utilisateur
     */
    private String email;
    
    /**
     * Mot de passe hashé (ignoré dans la sérialisation JSON)
     */
    @JsonIgnore
    private String password;
    
    /**
     * Rôle de l'utilisateur (CLIENT, VENDOR, ADMIN)
     */
    private String role;
    
    /**
     * Indique si le compte est actif
     */
    private Boolean isActive;
    
    /**
     * Constructeur à partir d'une entité User
     * @param user Entité User de notre modèle
     */
    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().name();
        this.isActive = user.getIsActive();
    }
    
    /**
     * Retourne les autorités (rôles) de l'utilisateur
     * @return Collection d'autorités
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Crée une autorité basée sur le rôle avec le préfixe "ROLE_"
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role));
    }
    
    /**
     * Retourne le mot de passe de l'utilisateur
     * @return Mot de passe hashé
     */
    @Override
    public String getPassword() {
        return password;
    }
    
    /**
     * Retourne le nom d'utilisateur
     * @return Nom d'utilisateur
     */
    @Override
    public String getUsername() {
        return username;
    }
    
    /**
     * Indique si le compte n'a pas expiré
     * @return true (pas d'expiration implémentée)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    /**
     * Indique si le compte n'est pas verrouillé
     * @return true (pas de verrouillage implémenté)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    /**
     * Indique si les credentials n'ont pas expiré
     * @return true (pas d'expiration implémentée)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    /**
     * Indique si le compte est activé
     * @return true si le compte est actif
     */
    @Override
    public boolean isEnabled() {
        return isActive;
    }
    
    /**
     * Vérifie l'égalité avec un autre objet
     * @param obj Objet à comparer
     * @return true si les objets sont égaux
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserDetailsImpl user = (UserDetailsImpl) obj;
        return Objects.equals(id, user.id);
    }
    
    /**
     * Génère le hash code
     * @return Hash code basé sur l'ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * Retourne une représentation textuelle de l'utilisateur
     * @return String représentant l'utilisateur
     */
    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                '}';
    }
} 