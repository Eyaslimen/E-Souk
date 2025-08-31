package com.example.e_souk.Repository;

import com.example.e_souk.Model.Commande;
import com.example.e_souk.Model.User;
import com.example.e_souk.Model.Shop;
import com.example.e_souk.Model.EtatCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository pour la gestion des commandes
 * Fournit les méthodes d'accès aux données pour les commandes
 */
@Repository
public interface CommandeRepository extends JpaRepository<Commande, UUID> {
     /**
     * Trouve toutes les commandes d'un utilisateur triées par date de création décroissante
     */
    List<Commande> findByUserOrderByCreatedAtDesc(User user);
    /**
     * Trouve toutes les commandes d'un utilisateur
     * @param user Utilisateur
     * @return Liste des commandes de l'utilisateur
     */
    List<Commande> findByUser(User user);
    
    /**
     * Trouve toutes les commandes d'un utilisateur par son ID
     * @param userId ID de l'utilisateur
     * @return Liste des commandes de l'utilisateur
     */
    List<Commande> findByUserId(UUID userId);
    
    /**
     * Trouve toutes les commandes d'un utilisateur avec pagination
     * @param userId ID de l'utilisateur
     * @param pageable Paramètres de pagination
     * @return Page des commandes de l'utilisateur
     */
    Page<Commande> findByUserId(UUID userId, Pageable pageable);
    
    /**
     * Trouve toutes les commandes d'une boutique
     * @param shop Boutique
     * @return Liste des commandes de la boutique
     */
    List<Commande> findByShop(Shop shop);
    
    /**
     * Trouve toutes les commandes d'une boutique par son ID
     * @param shopId ID de la boutique
     * @return Liste des commandes de la boutique
     */
    List<Commande> findByShopId(UUID shopId);
    
    /**
     * Trouve toutes les commandes d'une boutique avec pagination
     * @param shopId ID de la boutique
     * @param pageable Paramètres de pagination
     * @return Page des commandes de la boutique
     */
    Page<Commande> findByShopId(UUID shopId, Pageable pageable);
    
    /**
     * Trouve les commandes par état
     * @param etat État de la commande
     * @return Liste des commandes avec cet état
     */
    List<Commande> findByEtat(EtatCommande etat);
    
    /**
     * Trouve les commandes d'un utilisateur par état
     * @param userId ID de l'utilisateur
     * @param etat État de la commande
     * @return Liste des commandes de l'utilisateur avec cet état
     */
    List<Commande> findByUserIdAndEtat(UUID userId, EtatCommande etat);
    
    /**
     * Trouve les commandes d'une boutique par état
     * @param shopId ID de la boutique
     * @param etat État de la commande
     * @return Liste des commandes de la boutique avec cet état
     */
    List<Commande> findByShopIdAndEtat(UUID shopId, EtatCommande etat);
    
    /**
     * Trouve une commande par son numéro
     * @param orderNumber Numéro de commande
     * @return Commande trouvée
     */
    Commande findByOrderNumber(String orderNumber);
    
    /**
     * Trouve les commandes créées entre deux dates
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des commandes créées dans cette période
     */
    List<Commande> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve les commandes d'un utilisateur créées entre deux dates
     * @param userId ID de l'utilisateur
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des commandes de l'utilisateur créées dans cette période
     */
    List<Commande> findByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Compte le nombre de commandes d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Nombre de commandes
     */
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") UUID userId);
    
    /**
     * Compte le nombre de commandes d'une boutique
     * @param shopId ID de la boutique
     * @return Nombre de commandes
     */
    @Query("SELECT COUNT(c) FROM Commande c WHERE c.shop.id = :shopId")
    Long countByShopId(@Param("shopId") UUID shopId);
    
    /**
     * Calcule le montant total des commandes d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Montant total
     */
    @Query("SELECT SUM(c.total) FROM Commande c WHERE c.user.id = :userId")
    Double getTotalAmountByUserId(@Param("userId") UUID userId);
    
    /**
     * Calcule le montant total des commandes d'une boutique
     * @param shopId ID de la boutique
     * @return Montant total
     */
    @Query("SELECT SUM(c.total) FROM Commande c WHERE c.shop.id = :shopId")
    Double getTotalAmountByShopId(@Param("shopId") UUID shopId);
    
    /**
     * Trouve les commandes d'un utilisateur avec les détails des articles
     * @param userId ID de l'utilisateur
     * @return Liste des commandes avec articles
     */
    @Query("SELECT c FROM Commande c JOIN FETCH c.orderItems oi JOIN FETCH oi.variant v JOIN FETCH v.product p WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Commande> findByUserIdWithOrderItems(@Param("userId") UUID userId);
}
