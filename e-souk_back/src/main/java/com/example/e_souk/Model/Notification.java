package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant une notification dans la marketplace
 * Permet d'envoyer des notifications aux utilisateurs pour différents événements
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    
    /**
     * Identifiant unique de la notification
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Type de notification
     * Définit le type d'événement qui a déclenché la notification
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    @NotNull(message = "Le type de notification est obligatoire")
    private NotificationType type;
    
    /**
     * Titre de la notification
     * Titre court et accrocheur
     */
    @Column(name = "title", nullable = false, length = 100)
    @NotBlank(message = "Le titre de la notification est obligatoire")
    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    private String title;
    
    /**
     * Message de la notification
     * Contenu détaillé de la notification
     */
    @Column(name = "message", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Le message ne peut pas dépasser 1000 caractères")
    private String message;
    
    /**
     * Nom de l'expéditeur
     * Nom de la personne ou entité qui a déclenché la notification
     */
    @Column(name = "sender_name", length = 100)
    @Size(max = 100, message = "Le nom de l'expéditeur ne peut pas dépasser 100 caractères")
    private String senderName;
    
    /**
     * Photo de l'expéditeur
     * URL vers l'image de l'expéditeur
     */
    @Column(name = "sender_picture", length = 255)
    @Size(max = 255, message = "L'URL de l'image ne peut pas dépasser 255 caractères")
    private String senderPicture;
    
    /**
     * Indique si la notification a été lue
     * Permet de gérer l'état de lecture des notifications
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    /**
     * Date de création de la notification
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Utilisateur qui reçoit la notification
     * Relation ManyToOne : plusieurs notifications peuvent être reçues par un utilisateur
     * Fetch EAGER : on charge toujours l'utilisateur car c'est une info importante
     * Cascade PERSIST : si on sauvegarde une notification, l'utilisateur est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "L'utilisateur de la notification est obligatoire")
    private User user;
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Marque la notification comme lue
     * Met à jour l'état de lecture
     */
    public void markAsRead() {
        this.isRead = true;
    }
    
    /**
     * Marque la notification comme non lue
     * Met à jour l'état de lecture
     */
    public void markAsUnread() {
        this.isRead = false;
    }
    
    /**
     * Vérifie si la notification est non lue
     * @return true si la notification n'a pas encore été lue
     */
    public Boolean isUnread() {
        return !isRead;
    }
    
    /**
     * Calcule l'âge de la notification en minutes
     * @return Nombre de minutes depuis la création
     */
    public Long getAgeInMinutes() {
        if (createdAt == null) {
            return 0L;
        }
        
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    }
    
    /**
     * Calcule l'âge de la notification en heures
     * @return Nombre d'heures depuis la création
     */
    public Long getAgeInHours() {
        if (createdAt == null) {
            return 0L;
        }
        
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
    }
    
    /**
     * Calcule l'âge de la notification en jours
     * @return Nombre de jours depuis la création
     */
    public Long getAgeInDays() {
        if (createdAt == null) {
            return 0L;
        }
        
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
    }
    
    /**
     * Vérifie si la notification est récente (moins de 1 heure)
     * @return true si la notification date de moins d'1 heure
     */
    public Boolean isRecent() {
        return getAgeInMinutes() < 60;
    }
    
    /**
     * Vérifie si la notification est ancienne (plus de 7 jours)
     * @return true si la notification date de plus de 7 jours
     */
    public Boolean isOld() {
        return getAgeInDays() > 7;
    }
    
    /**
     * Récupère une description formatée de l'âge de la notification
     * @return Description relative du temps écoulé
     */
    public String getRelativeTime() {
        Long minutes = getAgeInMinutes();
        Long hours = getAgeInHours();
        Long days = getAgeInDays();
        
        if (minutes < 1) {
            return "À l'instant";
        } else if (minutes < 60) {
            return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (hours < 24) {
            return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");
        } else {
            return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
        }
    }
    
    /**
     * Récupère le nom de l'utilisateur qui reçoit la notification
     * @return Nom d'utilisateur
     */
    public String getRecipientName() {
        return user != null ? user.getUsername() : "";
    }
    
    /**
     * Récupère l'email de l'utilisateur qui reçoit la notification
     * @return Email de l'utilisateur
     */
    public String getRecipientEmail() {
        return user != null ? user.getEmail() : "";
    }
    
    /**
     * Récupère une description du type de notification
     * @return Description lisible du type
     */
    public String getTypeDescription() {
        switch (type) {
            case AlertCommandeRecue:
                return "Nouvelle commande reçue";
            case AlertStockBas:
                return "Stock bas détecté";
            case AvisAjouteRecu:
                return "Nouvel avis reçu";
            case AlertStockReapprovisionne:
                return "Stock réapprovisionné";
            case CommandeExpediee:
                return "Commande expédiée";
            case CommandeLivree:
                return "Commande livrée";
            default:
                return "Notification";
        }
    }
    
    /**
     * Récupère une icône pour le type de notification
     * @return Emoji ou symbole représentant le type
     */
    public String getTypeIcon() {
        switch (type) {
            case AlertCommandeRecue:
                return "🛒";
            case AlertStockBas:
                return "⚠️";
            case AvisAjouteRecu:
                return "⭐";
            case AlertStockReapprovisionne:
                return "📦";
            case CommandeExpediee:
                return "🚚";
            case CommandeLivree:
                return "✅";
            default:
                return "📢";
        }
    }
} 