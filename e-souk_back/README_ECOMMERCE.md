# Fonctionnalités E-Commerce - E-Souk Backend

Ce document décrit les nouvelles fonctionnalités e-commerce ajoutées au backend d'E-Souk.

## 🛒 Gestion du Panier

### Endpoints API

- `GET /api/cart/{userId}` - Récupérer le panier d'un utilisateur
- `POST /api/cart/{userId}/items` - Ajouter un article au panier
- `PUT /api/cart/{userId}/items/{variantId}` - Mettre à jour la quantité d'un article
- `DELETE /api/cart/{userId}/items/{variantId}` - Supprimer un article du panier
- `DELETE /api/cart/{userId}` - Vider le panier

### Fonctionnalités

- Ajout/suppression d'articles
- Mise à jour des quantités
- Calcul automatique du total
- Gestion des variantes de produits
- Vérification du stock

## ❤️ Gestion des Favoris

### Endpoints API

- `GET /api/favorites/{userId}` - Récupérer les favoris d'un utilisateur
- `POST /api/favorites/{userId}/products/{productId}` - Ajouter un produit aux favoris
- `DELETE /api/favorites/{userId}/products/{productId}` - Supprimer un produit des favoris
- `GET /api/favorites/{userId}/products/{productId}/check` - Vérifier si un produit est en favori
- `GET /api/favorites/{userId}/count` - Compter les favoris

### Fonctionnalités

- Ajout/suppression de produits favoris
- Vérification de l'état favori
- Comptage des favoris
- Informations détaillées des produits

## 📦 Gestion des Commandes

### Endpoints API

- `GET /api/orders/user/{userId}` - Récupérer les commandes d'un utilisateur
- `GET /api/orders/{orderId}` - Récupérer une commande par ID
- `POST /api/orders/{userId}/create` - Créer une commande depuis le panier
- `PUT /api/orders/{orderId}/status` - Mettre à jour le statut d'une commande
- `PUT /api/orders/{orderId}/cancel` - Annuler une commande

### États des Commandes

- `EnAttente` - Commande en attente de traitement
- `EnCours` - Commande en cours de préparation
- `Expediee` - Commande expédiée
- `Livree` - Commande livrée
- `Annulee` - Commande annulée

### Fonctionnalités

- Création de commandes depuis le panier
- Gestion des états de commande
- Calcul automatique des totaux
- Gestion des frais de livraison
- Historique des commandes

## 👥 Suivi des Boutiques

### Endpoints API

- `GET /api/shop-followers/user/{userId}` - Récupérer les boutiques suivies
- `GET /api/shop-followers/shop/{shopId}` - Récupérer les followers d'une boutique
- `POST /api/shop-followers/{userId}/shops/{shopId}` - Suivre une boutique
- `DELETE /api/shop-followers/{userId}/shops/{shopId}` - Ne plus suivre une boutique
- `GET /api/shop-followers/{userId}/shops/{shopId}/check` - Vérifier si on suit une boutique
- `GET /api/shop-followers/shop/{shopId}/count` - Compter les followers d'une boutique
- `GET /api/shop-followers/user/{userId}/count` - Compter les boutiques suivies

### Fonctionnalités

- Suivre/ne plus suivre des boutiques
- Vérification de l'état de suivi
- Comptage des followers
- Informations détaillées des boutiques

## 📊 Statistiques Utilisateur

### Endpoint API

- `GET /api/users/{userId}/stats` - Récupérer les statistiques e-commerce

### Données Retournées

```json
{
  "favoriteCount": 5,
  "followedShopCount": 3,
  "cartItemCount": 2,
  "cartTotalAmount": 89.99
}
```

## 🔧 Services Backend

### CartService
- Gestion complète du panier
- Ajout/suppression d'articles
- Calcul des totaux
- Vérification du stock

### ProductFavoriteService
- Gestion des produits favoris
- Vérification des doublons
- Comptage des favoris

### CommandeService
- Création de commandes
- Gestion des états
- Calcul des totaux
- Historique des commandes

### ShopFollowerService
- Gestion des followers
- Vérification des doublons
- Comptage des followers

## 📁 Structure des DTOs

### CartDTO
```java
public class CartDTO {
    private UUID id;
    private UUID userId;
    private List<CartItemDTO> items;
    private Integer itemCount;
    private Long uniqueItemCount;
    private Double totalAmount;
    private Boolean isEmpty;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### ProductFavoriteDTO
```java
public class ProductFavoriteDTO {
    private UUID id;
    private UUID userId;
    private UUID productId;
    private String productName;
    private String productImage;
    private Double productPrice;
    private Double productAverageRating;
    private Boolean productInStock;
    private UUID shopId;
    private String shopName;
    private String shopCategory;
    private LocalDateTime addedAt;
    private Long favoriteDurationInDays;
    private Boolean isRecentFavorite;
}
```

### CommandeDTO
```java
public class CommandeDTO {
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private String customerName;
    private UUID shopId;
    private String shopName;
    private String deliveryAddress;
    private String deliveryPostalCode;
    private Float total;
    private Float deliveryFee;
    private Float subtotal;
    private EtatCommande etat;
    private Integer totalItemCount;
    private Long uniqueItemCount;
    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private List<OrderItemDTO> orderItems;
}
```

### ShopFollowerDTO
```java
public class ShopFollowerDTO {
    private UUID id;
    private UUID userId;
    private String userName;
    private String userEmail;
    private String userPicture;
    private UUID shopId;
    private String shopName;
    private String shopBrandName;
    private String shopCategory;
    private String shopDescription;
    private String shopLogo;
    private LocalDateTime followedAt;
    private Long followDurationInDays;
    private Boolean isRecentFollower;
}
```

## 🚀 Intégration Frontend

Le frontend Angular a été mis à jour avec :

1. **Service UserEcommerceService** - Communication avec l'API
2. **Interfaces TypeScript** - Typage des données
3. **Composant Profile mis à jour** - Utilisation des vraies données
4. **Gestion d'erreurs** - Affichage des messages d'erreur
5. **Chargement asynchrone** - Indicateurs de chargement

## 🔐 Sécurité

- Tous les endpoints sont protégés par Spring Security
- Vérification des permissions utilisateur
- Validation des données d'entrée
- Gestion des exceptions

## 📝 Notes d'Implémentation

1. **UUID** - Utilisation d'UUID pour tous les identifiants
2. **Audit** - Dates de création/modification automatiques
3. **Relations JPA** - Relations optimisées avec FetchType.LAZY
4. **Validation** - Validation des données avec Bean Validation
5. **Logging** - Logs détaillés pour le debugging

## 🧪 Tests

Pour tester les fonctionnalités :

1. Démarrer le backend Spring Boot
2. Utiliser Postman ou un client HTTP
3. Tester les endpoints avec des données valides
4. Vérifier les réponses et les codes d'état HTTP

## 🔄 Prochaines Étapes

1. Ajouter des tests unitaires
2. Implémenter la pagination pour les listes
3. Ajouter des filtres et tris
4. Optimiser les performances avec du cache
5. Ajouter des notifications en temps réel
