package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entité représentant un utilisateur dans la marketplace
 * Peut être un CLIENT, VENDOR ou ADMIN selon son rôle
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    /**
     * Identifiant unique de l'utilisateur
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Nom d'utilisateur unique pour la connexion
     * Doit être unique dans toute la plateforme
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;
    
    /**
     * Adresse email unique de l'utilisateur
     * Utilisée pour la connexion et les communications
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;
    
    /**
     * Mot de passe hashé de l'utilisateur
     * Ne doit jamais être stocké en clair
     */
    @Column(name = "password", nullable = false)
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
    
    /**
     * Photo de profil de l'utilisateur
     * URL vers l'image stockée
     */
    @Column(name = "picture", length = 255)
    @Size(max = 255, message = "L'URL de l'image ne peut pas dépasser 255 caractères")
    private String picture;
    
    /**
     * Numéro de téléphone de l'utilisateur
     * Optionnel mais utile pour les livraisons
     */
    @Column(name = "phone", length = 20)
    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String phone;
    
    /**
     * Adresse postale de l'utilisateur
     * Utilisée pour les livraisons
     */
    @Column(name = "address", length = 255)
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String address;
    
    /**
     * Code postal de l'utilisateur
     * Utilisé pour les calculs de livraison
     */
    @Column(name = "code_postal", length = 10)
    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    private String codePostal;
    
    /**
     * Indique si le compte utilisateur est actif
     * Permet de désactiver temporairement un compte sans le supprimer
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * Rôle de l'utilisateur dans la plateforme
     * Détermine les permissions et fonctionnalités accessibles
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @NotNull(message = "Le rôle est obligatoire")
    private Role role = Role.CLIENT;
    
    /**
     * Date de création du compte utilisateur
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    /**
     * Date de dernière modification du profil
     * Mise à jour automatiquement par Spring Data JPA
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Boutiques possédées par cet utilisateur (si VENDOR)
     * Relation OneToMany : un utilisateur peut posséder plusieurs boutiques
     * Cascade PERSIST : si on sauvegarde un utilisateur, ses boutiques sont aussi sauvegardées
     * Fetch LAZY : on ne charge les boutiques que si nécessaire (optimisation performance)
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Shop> shops = new ArrayList<>();
    
    /**
     * Panier de l'utilisateur
     * Relation OneToOne : chaque utilisateur a exactement un panier
     * Cascade ALL : si on supprime un utilisateur, son panier est aussi supprimé
     * Fetch LAZY : on ne charge le panier que si nécessaire
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;
    
    /**
     * Commandes passées par cet utilisateur
     * Relation OneToMany : un utilisateur peut passer plusieurs commandes
     * Cascade PERSIST : si on sauvegarde un utilisateur, ses commandes sont aussi sauvegardées
     * Fetch LAZY : on ne charge les commandes que si nécessaire
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Commande> commandes = new ArrayList<>();
    
    /**
     * Avis écrits par cet utilisateur
     * Relation OneToMany : un utilisateur peut écrire plusieurs avis
     * Cascade PERSIST : si on sauvegarde un utilisateur, ses avis sont aussi sauvegardés
     * Fetch LAZY : on ne charge les avis que si nécessaire
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    /**
     * Notifications reçues par cet utilisateur
     * Relation OneToMany : un utilisateur peut recevoir plusieurs notifications
     * Cascade ALL : si on supprime un utilisateur, ses notifications sont aussi supprimées
     * Fetch LAZY : on ne charge les notifications que si nécessaire
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Récupère l'historique des commandes de l'utilisateur
     * @return Liste des commandes triées par date de création (plus récentes en premier)
     */
    public List<Commande> getOrderHistory() {
        return commandes.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
    }
    
    /**
     * Récupère les produits favoris de l'utilisateur
     * @return Liste des produits favoris
     */
    public List<Product> getFavoriteProducts() {
        // Cette méthode sera implémentée quand on aura la relation avec ProductFavorite
        // Pour l'instant, on retourne une liste vide
        // TODO: Implémenter avec ProductFavorite
        return new ArrayList<>();
    }
    
    /**
     * Récupère les boutiques suivies par l'utilisateur
     * @return Liste des boutiques suivies
     */
    public List<Shop> getFollowedShops() {
        // Cette méthode sera implémentée quand on aura la relation avec ShopFollower
        // Pour l'instant, on retourne une liste vide
        // TODO: Implémenter avec ShopFollower
        return new ArrayList<>();
    }
} 