package com.example.e_souk.Service;

import com.example.e_souk.Dto.Commande.CommandeDTO;
import com.example.e_souk.Dto.Commande.OrderItemDTO;
import com.example.e_souk.Dto.Commande.ShopOrdersDto;
import com.example.e_souk.Dto.Commande.UserOrdersDto;
import com.example.e_souk.Exception.ResourceNotFoundException;
import com.example.e_souk.Model.*;
import com.example.e_souk.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des commandes
 * Contient la logique métier pour les opérations sur les commandes
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommandeService {
    
    private final CommandeRepository commandeRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    
    /**
     * Récupère toutes les commandes d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des DTOs des commandes
     */
    public List<CommandeDTO> getUserOrders(UUID userId) {
        log.debug("Récupération des commandes pour l'utilisateur: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        List<Commande> commandes = commandeRepository.findByUserIdWithOrderItems(userId);
        
        return commandes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les commandes d'un utilisateur avec pagination
     * @param userId ID de l'utilisateur
     * @param pageable Paramètres de pagination
     * @return Page des DTOs des commandes
     */
    public Page<CommandeDTO> getUserOrdersPaginated(UUID userId, Pageable pageable) {
        log.debug("Récupération des commandes paginées pour l'utilisateur: {}", userId);
        
        Page<Commande> commandes = commandeRepository.findByUserId(userId, pageable);
        
        return commandes.map(this::convertToDTO);
    }
    
    /**
     * Récupère une commande par son ID
     * @param orderId ID de la commande
     * @return DTO de la commande
     */
    public CommandeDTO getOrderById(UUID orderId) {
        log.debug("Récupération de la commande: {}", orderId);
        
        Commande commande = commandeRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
        
        return convertToDTO(commande);
    }
    
    /**
     * Crée une nouvelle commande à partir du panier
     * @param userId ID de l'utilisateur
     * @param shopId ID de la boutique
     * @param deliveryAddress Adresse de livraison
     * @param deliveryPostalCode Code postal de livraison
     * @return DTO de la commande créée
     */
    public CommandeDTO createOrderFromCart(UUID userId, UUID shopId, String deliveryAddress, String deliveryPostalCode) {
        log.info("Création d'une commande à partir du panier - Utilisateur: {}, Boutique: {}", userId, shopId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Boutique non trouvée"));
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Panier non trouvé"));
        
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        
        // Filtrer les articles de la boutique spécifiée
        List<CartItem> shopItems = cartItems.stream()
                .filter(item -> item.getVariant().getProduct().getShop().getId().equals(shopId))
                .collect(Collectors.toList());
        
        if (shopItems.isEmpty()) {
            throw new IllegalArgumentException("Aucun article de cette boutique dans le panier");
        }
        
        // Créer la commande
        Commande commande = Commande.builder()
                .orderNumber(Commande.generateOrderNumber())
                .user(user)
                .shop(shop)
                .deliveryAddress(deliveryAddress)
                .etat(EtatCommande.EnAttente)
                .deliveryFee(shop.getDeliveryFee())
                .build();
        
        // Créer les articles de commande
        List<OrderItem> orderItems = shopItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .commande(commande)
                        .variant(cartItem.getVariant())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getVariant().getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());
        
        commande.setOrderItems(orderItems);
        
        // Calculer le total
        Float subtotal = orderItems.stream()
                .map(item -> item.getQuantity() * item.getUnitPrice())
                .reduce(0f, Float::sum);
        commande.setTotal(subtotal + shop.getDeliveryFee());
        
        Commande savedCommande = commandeRepository.save(commande);
        
        // Supprimer les articles du panier
        shopItems.forEach(cartItemRepository::delete);
        
        log.info("Commande créée avec succès: {}", savedCommande.getOrderNumber());
        
        return convertToDTO(savedCommande);
    }
    
    /**
     * Met à jour l'état d'une commande
     * @param orderId ID de la commande
     * @param newEtat Nouvel état
     * @return DTO de la commande mise à jour
     */
    public CommandeDTO updateOrderStatus(UUID orderId, EtatCommande newEtat) {
        log.info("Mise à jour de l'état de la commande: {} -> {}", orderId, newEtat);
        
        Commande commande = commandeRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
        
        switch (newEtat) {
            case EnCours:
                commande.setEtat(EtatCommande.EnCours);
                break;
            case Expediee:
                commande.markAsShipped();
                break;
            case Livree:
                commande.markAsDelivered();
                break;
            case Annulee:
                commande.cancel();
                break;
            default:
                commande.setEtat(newEtat);
        }
        
        Commande updatedCommande = commandeRepository.save(commande);
        return convertToDTO(updatedCommande);
    }
    
    /**
     * Annule une commande
     * @param orderId ID de la commande
     * @return DTO de la commande annulée
     */
    public CommandeDTO cancelOrder(UUID orderId) {
        log.info("Annulation de la commande: {}", orderId);
        
        Commande commande = commandeRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
        
        if (!commande.canBeCancelled()) {
            throw new IllegalArgumentException("La commande ne peut pas être annulée");
        }
        
        commande.cancel();
        Commande cancelledCommande = commandeRepository.save(commande);
        
        return convertToDTO(cancelledCommande);
    }
    
    /**
     * Convertit une commande en DTO
     * @param commande Commande à convertir
     * @return DTO de la commande
     */
    private CommandeDTO convertToDTO(Commande commande) {
        List<OrderItemDTO> orderItemDTOs = commande.getOrderItems().stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());
        
        return CommandeDTO.builder()
                .id(commande.getId())
                .orderNumber(commande.getOrderNumber())
                .userId(commande.getUser().getId())
                .customerName(commande.getCustomerName())
                .shopId(commande.getShop().getId())
                .shopName(commande.getShopName())
                .deliveryAddress(commande.getDeliveryAddress())
                .total(commande.getTotal())
                .deliveryFee(commande.getDeliveryFee())
                .subtotal(commande.getSubtotal())
                .etat(commande.getEtat())
                .totalItemCount(commande.getTotalItemCount())
                .uniqueItemCount(commande.getUniqueItemCount())
                .createdAt(commande.getCreatedAt())
                .shippedAt(commande.getShippedAt())
                .deliveredAt(commande.getDeliveredAt())
                .orderItems(orderItemDTOs)
                .build();
    }
    
    /**
     * Convertit un article de commande en DTO
     * @param orderItem Article de commande à convertir
     * @return DTO de l'article
     */
    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .commandeId(orderItem.getCommande().getId())
                .variantId(orderItem.getVariant().getId())
                .productId(orderItem.getVariant().getProduct().getId())
                .productName(orderItem.getVariant().getProduct().getName())
                .productImage(orderItem.getVariant().getProduct().getPicture())
                .variantName(orderItem.getVariant().getSku())
                .price(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .subTotal(orderItem.getSubTotal())
                .shopName(orderItem.getVariant().getProduct().getShop().getBrandName())
                .shopId(orderItem.getVariant().getProduct().getShop().getId())
                .build();
    }


    /**
 * Récupère toutes les commandes de l'utilisateur groupées par boutique
 */
public UserOrdersDto getUserOrdersByShop(UUID userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    
    List<Commande> userOrders = commandeRepository.findByUserOrderByCreatedAtDesc(user);
    
    if (userOrders.isEmpty()) {
        return UserOrdersDto.builder()
                .shopOrders(List.of())
                .totalOrders(0)
                .shopCount(0)
                .build();
    }
    
    // Grouper les commandes par boutique
    Map<UUID, List<Commande>> ordersByShop = userOrders.stream()
            .collect(Collectors.groupingBy(commande -> commande.getShop().getId()));
    
    List<ShopOrdersDto> shopOrders = ordersByShop.entrySet().stream()
            .map(entry -> {
                UUID shopId = entry.getKey();
                List<Commande> shopCommandes = entry.getValue();
                
                String shopName = shopCommandes.get(0).getShop().getBrandName();
                
                List<CommandeDTO> orderDtos = shopCommandes.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
                
                return ShopOrdersDto.builder()
                        .shopId(shopId)
                        .shopName(shopName)
                        .orders(orderDtos)
                        .orderCount(orderDtos.size())
                        .build();
            })
            .collect(Collectors.toList());
    
    return UserOrdersDto.builder()
            .shopOrders(shopOrders)
            .totalOrders(userOrders.size())
            .shopCount(shopOrders.size())
            .build();
}
}
