package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant l'abonnement d'un utilisateur à une boutique
 * Table de liaison entre User et Shop pour gérer les abonnements
 */
@Entity
@Table(name = "shop_followers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ShopFollower {
    
    /**
     * Identifiant unique de l'abonnement
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Date d'abonnement à la boutique
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "followed_at", nullable = false, updatable = false)
    private LocalDateTime followedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Utilisateur qui suit la boutique
     * Relation ManyToOne : plusieurs abonnements peuvent appartenir à un utilisateur
     * Fetch EAGER : on charge toujours l'utilisateur car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un abonnement, l'utilisateur est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "L'utilisateur est obligatoire")
    private User user;
    
    /**
     * Boutique suivie par l'utilisateur
     * Relation ManyToOne : plusieurs abonnements peuvent pointer vers une boutique
     * Fetch EAGER : on charge toujours la boutique car c'est une info importante
     * Cascade PERSIST : si on sauvegarde un abonnement, la boutique est aussi sauvegardée
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "shop_id", nullable = false)
    @NotNull(message = "La boutique est obligatoire")
    private Shop shop;
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Récupère le nom d'utilisateur
     * @return Nom d'utilisateur
     */
    public String getUsername() {
        return user != null ? user.getUsername() : "";
    }
    
    /**
     * Récupère le nom de la boutique
     * @return Nom de la boutique
     */
    public String getShopName() {
        return shop != null ? shop.getBrandName() : "";
    }
    
    /**
     * Récupère l'email de l'utilisateur
     * @return Email de l'utilisateur
     */
    public String getUserEmail() {
        return user != null ? user.getEmail() : "";
    }
    
    /**
     * Récupère la photo de profil de l'utilisateur
     * @return URL de la photo de profil
     */
    public String getUserPicture() {
        return user != null ? user.getPicture() : "";
    }
    
    /**
     * Récupère le logo de la boutique
     * @return URL du logo de la boutique
     */
    public String getShopLogo() {
        return shop != null ? shop.getLogoPicture() : "";
    }
    
    /**
     * Calcule la durée de l'abonnement en jours
     * @return Nombre de jours depuis l'abonnement
     */
    public Long getFollowDurationInDays() {
        if (followedAt == null) {
            return 0L;
        }
        
        return java.time.Duration.between(followedAt, LocalDateTime.now()).toDays();
    }
    
    /**
     * Vérifie si l'abonnement est récent (moins de 7 jours)
     * @return true si l'abonnement date de moins de 7 jours
     */
    public Boolean isRecentFollower() {
        return getFollowDurationInDays() < 7;
    }
} 