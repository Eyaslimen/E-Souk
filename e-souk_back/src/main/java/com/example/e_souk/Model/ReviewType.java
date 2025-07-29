package com.example.e_souk.Model;

/**
 * Énumération des types d'avis dans la marketplace
 * Permet de distinguer les avis sur les boutiques des avis sur les produits
 */
public enum ReviewType {
    /**
     * Avis portant sur une boutique entière
     */
    SHOP,
    
    /**
     * Avis portant sur un produit spécifique
     */
    PRODUCT
} 