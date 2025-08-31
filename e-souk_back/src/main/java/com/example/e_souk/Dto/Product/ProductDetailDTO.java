package com.example.e_souk.Dto.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO pour afficher les détails d'un produit avec toutes les options d'attributs disponibles
 * Utilisé pour permettre à l'utilisateur de choisir les variantes avant d'ajouter au panier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDTO {
    
    /**
     * ID du produit
     */
    private UUID productId;
    
    /**
     * Nom du produit
     */
    private String name;
    
    /**
     * Description du produit
     */
    private String description;
    
    /**
     * Prix du produit
     */
    private Float price;
    
    /**
     * Image du produit
     */
    private String picture;
    
    /**
     * Nom de la boutique
     */
    private String shopName;
    

    /**
     * Map des attributs disponibles
     * Clé : Nom de l'attribut (ex: "Taille", "Couleur")
     * Valeur : Liste des valeurs possibles (ex: ["S", "M", "L", "XL"])
     */
    private Map<String, List<String>> availableAttributes = new HashMap<>();
    
}