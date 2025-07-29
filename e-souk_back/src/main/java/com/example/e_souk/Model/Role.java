package com.example.e_souk.Model;

/**
 * Énumération des rôles utilisateur dans la marketplace
 * Définit les trois niveaux d'accès possibles
 */
public enum Role {
    /**
     * Client : peut acheter, suivre des boutiques, laisser des avis
     */
    CLIENT,
    
    /**
     * Vendeur : peut créer des boutiques, gérer des produits et commandes
     */
    VENDOR,
    
    /**
     * Administrateur : accès complet à la plateforme
     */
    ADMIN
} 