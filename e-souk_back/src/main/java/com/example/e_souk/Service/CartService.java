package com.example.e_souk.Service;

import com.example.e_souk.Dto.Cart.AddToCartRequest;
import com.example.e_souk.Dto.Cart.CartDto;
import com.example.e_souk.Dto.Cart.CartItemDto;
import com.example.e_souk.Dto.Cart.ShopCartDto;
import com.example.e_souk.Model.*;
import com.example.e_souk.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion du panier
 * Contient la logique métier pour les opérations sur le panier
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final AttributeValueRepository attributeValueRepository;

    /**
     * Ajoute un article au panier de l'utilisateur
     * @param userId ID de l'utilisateur
     * @param request Données de l'article à ajouter
     * @return Article ajouté au panier
     */
    public CartItemDto addToCart(UUID userId, AddToCartRequest request) {
        
        // 1. Récupération et validation de l'utilisateur
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // 2. Récupération et validation du produit
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Ce produit n'est pas disponible");
        }

        // 3. Recherche de la variante correspondant aux attributs sélectionnés
        Variant variant = findVariantByAttributes(product, request.getSelectedAttributes());

        if (!variant.getIsActive()) {
            throw new IllegalArgumentException("Cette variante n'est pas disponible");
        }

        // 4. Vérification du stock
        if (!variant.hasEnoughStock(request.getQuantity())) {
            throw new IllegalArgumentException(
                String.format("Stock insuffisant. Stock disponible: %d", variant.getStock())
            );
        }
        // 5. Récupération ou création du panier
        Cart cart = getOrCreateCart(user);

        // 6. Vérification si l'article existe déjà dans le panier
        CartItem existingItem = cart.getItemByVariant(variant.getId());
        
        if (existingItem != null) {
            // Mise à jour de la quantité si l'article existe déjà
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            
            if (!variant.hasEnoughStock(newQuantity)) {
                throw new IllegalArgumentException(
                    String.format("Stock insuffisant pour cette quantité. Stock disponible: %d", variant.getStock())
                );
            }
            
            existingItem.setQuantity(newQuantity);
            CartItem cItem= cartItemRepository.save(existingItem);

            return toCartItemDto(cItem);
        } else {
            // Création d'un nouvel article
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            CartItem CartItem = cartItemRepository.save(newCartItem);
            return toCartItemDto(CartItem);
        }
    }

    /**
     * Met à jour la quantité d'un article dans le panier
     * @param userId ID de l'utilisateur
     * @param cartItemId ID de l'article du panier
     * @param quantity Nouvelle quantité
     * @return Article mis à jour
     */
    public CartItemDto updateCartItemQuantity(UUID userId, UUID cartItemId, Integer quantity) {
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }

        // Récupération de l'article du panier
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Article du panier non trouvé"));

        // Vérification que l'article appartient bien à l'utilisateur
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cet article ne vous appartient pas");
        }

        // Vérification du stock
        if (!cartItem.getVariant().hasEnoughStock(quantity)) {
            throw new IllegalArgumentException(
                String.format("Stock insuffisant. Stock disponible: %d", cartItem.getVariant().getStock())
            );
        }
        // Mise à jour de la quantité
        cartItem.setQuantity(quantity);
        return toCartItemDto(cartItemRepository.save(cartItem));
    }

    /**
     * Supprime un article du panier
     * @param userId ID de l'utilisateur
     * @param cartItemId ID de l'article à supprimer
     */
    public void removeFromCart(UUID userId, UUID cartItemId) {
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Article du panier non trouvé"));

        // Vérification que l'article appartient bien à l'utilisateur
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cet article ne vous appartient pas");
        }

        cartItemRepository.delete(cartItem);
    }

    /**
     * Vide complètement le panier de l'utilisateur
     * @param userId ID de l'utilisateur
     */
    public void clearCart(UUID userId) {
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Panier non trouvé"));

        cartItemRepository.deleteByCartId(cart.getId());
    }

    /**
     * Récupère le panier de l'utilisateur
     * @param userId ID de l'utilisateur
     * @return Panier de l'utilisateur
     */
    @Transactional(readOnly = true)
    public CartDto getCartByUserId(UUID userId) {
    
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

    Cart cart = getOrCreateCart(user);
    
    // Si le panier est vide
    if (cart.isEmpty()) {
        return CartDto.builder()
                .shopCarts(List.of())
                .totalPrice(0.0f)
                .totalItems(0)
                .shopCount(0)
                .build();
    }
    
    // Retourne une Map où la clé est l'ID de la boutique et la valeur est la liste des articles de cette boutique (cartItems)
    Map<UUID, List<CartItem>> itemsByShop = cart.getCartItems().stream()
            .collect(Collectors.groupingBy(
                item -> item.getVariant().getProduct().getShop().getId()
            ));
    
    // avoir une liste ShopCartDto pour chaque boutique  : prendre chaque element dans le map itemsByShop( idshop , list de cart Items lié a ce shop) et faire les changements nécessaires 
    List<ShopCartDto> shopCarts = itemsByShop.entrySet().stream()
            .map(entry -> { //entry içi est l'element du map itemsShop
                UUID shopId = entry.getKey();
                List<CartItem> shopItems = entry.getValue();
                
                // Prendre le premier item pour récupérer le nom de la boutique
                String shopName = shopItems.get(0).getVariant().getProduct().getShop().getBrandName();
                
                // Convertir les CartItem en CartItemDto
                List<CartItemDto> itemDtos = shopItems.stream()
                        .map(this::toCartItemDto)
                        .collect(Collectors.toList());
                
                // Calculer le total de la boutique
                Float shopTotal = shopItems.stream()
                        .map(CartItem::getSubTotal)
                        .reduce(0.0f, Float::sum);
                
                return ShopCartDto.builder()
                        .shopId(shopId)
                        .shopName(shopName)
                        .items(itemDtos)
                        .shopTotal(shopTotal)
                        .itemCount(shopItems.size())
                        .build();
            })
            .collect(Collectors.toList());
    
    // Créer le CartDto final : une cart qui contient # shopCarts ou chaque shop carts contient le nom du shop et les détails du produit lié a ce shop là 
    return CartDto.builder()
            .shopCarts(shopCarts)
            .totalPrice(cart.getTotalAmount())
            .totalItems(cart.getItemCount())
            .shopCount(shopCarts.size())
            .build();
}

    /**
     * Recherche la variante d'un produit correspondant aux attributs sélectionnés
     * @param product Produit
     * @param selectedAttributes Attributs sélectionnés
     * @return Variante correspondante
     */
    private Variant findVariantByAttributes(Product product, Map<String, String> selectedAttributes) {
        
        // Récupération de toutes les variantes actives du produit
        List<Variant> activeVariants = product.getVariants().stream()
                .filter(Variant::getIsActive)
                .collect(Collectors.toList());

        // Recherche de la variante qui correspond exactement aux attributs sélectionnés
        for (Variant variant : activeVariants) {
            if (variantMatchesAttributes(variant, selectedAttributes)) {
                return variant;
            }
        }

        throw new IllegalArgumentException("Aucune variante ne correspond aux attributs sélectionnés");
    }

    /**
     * Vérifie si une variante correspond aux attributs sélectionnés
     * @param variant Variante à vérifier
     * @param selectedAttributes Attributs sélectionnés
     * @return true si la variante correspond
     */
    private boolean variantMatchesAttributes(Variant variant, Map<String, String> selectedAttributes) {
        
        // Récupération des valeurs d'attributs de la variante
        Map<String, String> variantAttributes = variant.getAttributeValues().stream()
                .collect(Collectors.toMap(
                    av -> av.getAttribute().getName(),
                    AttributeValue::getValue
                ));

        // Vérification que tous les attributs sélectionnés correspondent
        for (Map.Entry<String, String> entry : selectedAttributes.entrySet()) {
            String attributeName = entry.getKey();
            String selectedValue = entry.getValue();
            String variantValue = variantAttributes.get(attributeName);

            if (variantValue == null || !variantValue.equals(selectedValue)) {
                return false;
            }
        }

        // Vérification que la variante n'a pas d'attributs supplémentaires
        return variantAttributes.size() == selectedAttributes.size();
    }

    /**
     * Récupère le panier de l'utilisateur ou en crée un nouveau
     * @param user Utilisateur
     * @return Panier de l'utilisateur
     */
    private Cart getOrCreateCart(User user) {
        
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }
    private CartItemDto toCartItemDto(CartItem cartItem) {
         CartItemDto cartItemDto = CartItemDto.builder()
                .id(cartItem.getId())
                .name(cartItem.getVariant().getProduct().getName())
                .price(cartItem.getVariant().getProduct().getPrice())
                .picture(cartItem.getVariant().getProduct().getPicture())
                .shopName(cartItem.getVariant().getProduct().getShop().getBrandName())
                .selectedAttributes(cartItem.getVariant().getAttributeValues().stream()
                        .collect(Collectors.toMap(
                                av -> av.getAttribute().getName(),
                                AttributeValue::getValue
                        )))
                .quantity(cartItem.getQuantity())
                .build();
        return cartItemDto;
    }
}