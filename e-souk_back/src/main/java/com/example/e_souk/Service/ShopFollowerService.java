package com.example.e_souk.Service;

import com.example.e_souk.Dto.Shop.ShopSummaryDTO;
import com.example.e_souk.Exception.ResourceNotFoundException;
import com.example.e_souk.Mappers.ProductMapper;
import com.example.e_souk.Mappers.ShopMapper;
import com.example.e_souk.Model.Shop;
import com.example.e_souk.Model.ShopFollower;
import com.example.e_souk.Model.User;
import com.example.e_souk.Repository.ShopFollowerRepository;
import com.example.e_souk.Repository.ShopRepository;
import com.example.e_souk.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des followers des boutiques
 * Contient la logique métier pour les opérations sur les followers
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShopFollowerService {
    
    private final ShopFollowerRepository shopFollowerRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    /**
     * Récupère toutes les boutiques suivies par un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des DTOs des boutiques suivies
     */
    public List<ShopSummaryDTO> getUserFollowedShops(UUID userId) {
        log.debug("Récupération des boutiques suivies pour l'utilisateur: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        List<ShopFollower> followers = shopFollowerRepository.findByUserIdWithShop(userId);
        List<ShopSummaryDTO> shops=new ArrayList<>();
        for (ShopFollower sf: followers) {
            Shop s = sf.getShop();
            ShopSummaryDTO sDTO = ShopMapper.toSummaryDTO(s,shopRepository.countProductsInShop(s.getId()), 0);
            shops.add(sDTO);
        }
    return shops;
    }
    // toSummaryDTO(shop, shopRepository.countProductsInShop(shop.getId()), 0));
    
    /**
     * Récupère tous les followers d'une boutique
     * @param shopId ID de la boutique
     * @return Liste des DTOs des followers
     */
    // type de retour de cette methode est faut ! on attend pas des shops ! on attend des utilisateurs
    public List<ShopSummaryDTO> getShopFollowers(UUID shopId) {
        log.debug("Récupération des followers pour la boutique: {}", shopId);
        
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Boutique non trouvée"));
        
        List<ShopFollower> followers = shopFollowerRepository.findByShopIdWithUser(shopId);
        
        List<ShopSummaryDTO> shops = new ArrayList<>();;
        for (ShopFollower sf: followers) {
            Shop s = sf.getShop();
            ShopSummaryDTO sDTO = ShopMapper.toSummaryDTO(s,shopRepository.countProductsInShop(s.getId()), 0);
            shops.add(sDTO);
        }
    return shops;
    }
    
    /**
     * Suit une boutique
     * @param userId ID de l'utilisateur
     * @param shopId ID de la boutique
     * @return DTO du follower créé
     */
    public ShopSummaryDTO followShop(UUID userId, UUID shopId) {
        log.info("Suivre une boutique - Utilisateur: {}, Boutique: {}", userId, shopId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Boutique non trouvée"));
        
        // Vérifier si l'utilisateur suit déjà cette boutique
        if (shopFollowerRepository.existsByUserIdAndShopId(userId, shopId)) {
            throw new IllegalArgumentException("Vous suivez déjà cette boutique");
        }
        
        ShopFollower follower = ShopFollower.builder()
                .user(user)
                .shop(shop)
                .build();
        
        ShopFollower savedFollower = shopFollowerRepository.save(follower);

        return ShopMapper.toSummaryDTO(shop,shopRepository.countProductsInShop(shop.getId()), 0);
    }
    
    /**
     * Ne suit plus une boutique
     * @param userId ID de l'utilisateur
     * @param shopId ID de la boutique
     */
    public void unfollowShop(UUID userId, UUID shopId) {
        log.info("Ne plus suivre une boutique - Utilisateur: {}, Boutique: {}", userId, shopId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Boutique non trouvée"));
        
        shopFollowerRepository.deleteByUserAndShop(user, shop);
    }
    
    /**
     * Vérifie si un utilisateur suit une boutique
     * @param userId ID de l'utilisateur
     * @param shopId ID de la boutique
     * @return true si l'utilisateur suit la boutique
     */
    public boolean isFollowingShop(UUID userId, UUID shopId) {
        return shopFollowerRepository.existsByUserIdAndShopId(userId, shopId);
    }
    
    /**
     * Compte le nombre de followers d'une boutique
     * @param shopId ID de la boutique
     * @return Nombre de followers
     */
    public Long getShopFollowerCount(UUID shopId) {
        return shopFollowerRepository.countByShopId(shopId);
    }
    


}
