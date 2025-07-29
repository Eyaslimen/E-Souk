package com.example.e_souk.Model;

/**
 * Énumération des types de notifications dans la marketplace
 * Définit tous les événements qui peuvent déclencher une notification
 */
public enum NotificationType {
    /**
     * Notification envoyée au vendeur quand une nouvelle commande est reçue
     */
    AlertCommandeRecue,
    
    /**
     * Notification envoyée au vendeur quand le stock d'un produit est bas
     */
    AlertStockBas,
    
    /**
     * Notification envoyée au vendeur quand un avis est ajouté sur sa boutique/produit
     */
    AvisAjouteRecu,
    
    /**
     * Notification envoyée au vendeur quand le stock est réapprovisionné
     */
    AlertStockReapprovisionne,
    
    /**
     * Notification envoyée au client quand sa commande est expédiée
     */
    CommandeExpediee,
    
    /**
     * Notification envoyée au client quand sa commande est livrée
     */
    CommandeLivree
} 