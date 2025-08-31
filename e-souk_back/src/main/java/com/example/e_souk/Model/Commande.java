package com.example.e_souk.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entité représentant une commande dans la marketplace
 * Chaque commande appartient à un utilisateur et une boutique, et contient plusieurs articles
 */
@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Commande {
    
    /**
     * Identifiant unique de la commande
     * Utilise UUID pour une meilleure distribution et sécurité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * Numéro de commande unique
     * Généré automatiquement pour l'identification client
     * Format: CMD-YYYYMMDD-XXXXX
     */
    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Le numéro de commande est obligatoire")
    @Size(max = 20, message = "Le numéro de commande ne peut pas dépasser 20 caractères")
    private String orderNumber;
    
    /**
     * Adresse de livraison
     * Adresse complète où livrer la commande
     */
    @Column(name = "delivery_address", nullable = false, length = 255)
    @NotBlank(message = "L'adresse de livraison est obligatoire")
    @Size(max = 255, message = "L'adresse de livraison ne peut pas dépasser 255 caractères")
    private String deliveryAddress;

    /**
     * Montant total de la commande
     * Inclut les articles + frais de livraison
     */
    @Column(name = "total", nullable = false)
    @NotNull(message = "Le montant total est obligatoire")
    @Min(value = 0, message = "Le montant total ne peut pas être négatif")
    private Float total;
    
    /**
     * Frais de livraison
     * Montant des frais de livraison de la boutique
     */
    @Column(name = "delivery_fee", nullable = false)
    @NotNull(message = "Les frais de livraison sont obligatoires")
    @Min(value = 0, message = "Les frais de livraison ne peuvent pas être négatifs")
    private Float deliveryFee = 0.0f;
    
    /**
     * État actuel de la commande
     * Définit le cycle de vie de la commande
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 20)
    @NotNull(message = "L'état de la commande est obligatoire")
    private EtatCommande etat = EtatCommande.EnAttente;
    
    /**
     * Date de création de la commande
     * Remplie automatiquement par Spring Data JPA
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Date d'expédition de la commande
     * Remplie quand la commande passe à l'état "Expédiée"
     */
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    /**
     * Date de livraison de la commande
     * Remplie quand la commande passe à l'état "Livrée"
     */
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    // ==================== RELATIONS JPA ====================
    
    /**
     * Utilisateur qui a passé la commande
     * Relation ManyToOne : plusieurs commandes peuvent appartenir à un utilisateur
     * Fetch EAGER : on charge toujours l'utilisateur car c'est une info importante
     * Cascade PERSIST : si on sauvegarde une commande, son utilisateur est aussi sauvegardé
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "L'utilisateur de la commande est obligatoire")
    private User user;
    
    /**
     * Boutique qui reçoit la commande
     * Relation ManyToOne : plusieurs commandes peuvent être reçues par une boutique
     * Fetch EAGER : on charge toujours la boutique car c'est une info importante
     * Cascade PERSIST : si on sauvegarde une commande, sa boutique est aussi sauvegardée
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "shop_id", nullable = false)
    @NotNull(message = "La boutique de la commande est obligatoire")
    private Shop shop;
    
    /**
     * Articles de la commande
     * Relation OneToMany : une commande peut contenir plusieurs articles
     * Cascade ALL : si on supprime une commande, ses articles sont aussi supprimés
     * Fetch LAZY : on ne charge les articles que si nécessaire (optimisation performance)
     */
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // ==================== MÉTHODES MÉTIER ====================
    
    /**
     * Calcule le montant total de la commande
     * Inclut le sous-total des articles + frais de livraison
     * @return Montant total calculé
     */
    public Float calculateTotal() {
        Float subtotal = getSubtotal();
        return subtotal + deliveryFee;
    }
    
    /**
     * Calcule le sous-total des articles (sans frais de livraison)
     * @return Sous-total des articles
     */
    public Float getSubtotal() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0.0f;
        }
        
        return (float) orderItems.stream()
                .mapToDouble(OrderItem::getSubTotal)
                .sum();
    }
    
    /**
     * Compte le nombre total d'articles dans la commande
     * @return Nombre total d'articles (somme des quantités)
     */
    public Integer getTotalItemCount() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0;
        }
        
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    /**
     * Compte le nombre de types d'articles différents dans la commande
     * @return Nombre de types d'articles différents
     */
    public Long getUniqueItemCount() {
        if (orderItems == null) {
            return 0L;
        }
        
        return orderItems.stream().count();
    }
    
    /**
     * Vérifie si la commande peut être expédiée
     * @return true si la commande est en cours et peut être expédiée
     */
    public Boolean canBeShipped() {
        return etat == EtatCommande.EnCours;
    }
    
    /**
     * Vérifie si la commande peut être livrée
     * @return true si la commande est expédiée et peut être livrée
     */
    public Boolean canBeDelivered() {
        return etat == EtatCommande.Expediee;
    }
    
    /**
     * Vérifie si la commande peut être annulée
     * @return true si la commande peut encore être annulée
     */
    public Boolean canBeCancelled() {
        return etat == EtatCommande.EnAttente || etat == EtatCommande.EnCours;
    }
    
    /**
     * Marque la commande comme expédiée
     * Met à jour l'état et la date d'expédition
     */
    public void markAsShipped() {
        if (canBeShipped()) {
            this.etat = EtatCommande.Expediee;
            this.shippedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Marque la commande comme livrée
     * Met à jour l'état et la date de livraison
     */
    public void markAsDelivered() {
        if (canBeDelivered()) {
            this.etat = EtatCommande.Livree;
            this.deliveredAt = LocalDateTime.now();
        }
    }
    
    /**
     * Annule la commande
     * Met à jour l'état
     */
    public void cancel() {
        if (canBeCancelled()) {
            this.etat = EtatCommande.Annulee;
        }
    }
    
    /**
     * Génère un numéro de commande unique
     * Format: CMD-YYYYMMDD-XXXXX
     * @return Numéro de commande généré
     */
    public static String generateOrderNumber() {
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%05d", (int) (Math.random() * 100000));
        return "CMD-" + date + "-" + random;
    }
    
    /**
     * Récupère le nom de l'utilisateur qui a passé la commande
     * @return Nom d'utilisateur
     */
    public String getCustomerName() {
        return user != null ? user.getUsername() : "";
    }
    
    /**
     * Récupère le nom de la boutique
     * @return Nom de la boutique
     */
    public String getShopName() {
        return shop != null ? shop.getBrandName() : "";
    }
} 