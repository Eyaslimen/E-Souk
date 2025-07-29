package com.example.e_souk.Model;

/**
 * Énumération des états possibles d'une commande
 * Définit le cycle de vie complet d'une commande dans la marketplace
 */
public enum EtatCommande {
    /**
     * Commande créée mais pas encore traitée par le vendeur
     */
    EnAttente,
    
    /**
     * Commande en cours de préparation par le vendeur
     */
    EnCours,
    
    /**
     * Commande expédiée au client
     */
    Expediee,
    
    /**
     * Commande livrée au client
     */
    Livree,
    
    /**
     * Commande annulée (par le client ou le vendeur)
     */
    Annulee
} 