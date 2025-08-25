package com.example.e_souk.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.e_souk.Dto.Category.CategoryRequestDTO;
import com.example.e_souk.Dto.Category.CategoryResponseDTO;
import com.example.e_souk.Dto.Product.ProductDetailsDTO;
import com.example.e_souk.Dto.Product.ProductFilterDTO;
import com.example.e_souk.Dto.Shop.CreateShopRequestDTO;
import com.example.e_souk.Dto.Shop.ShopDetailsDTO;
import com.example.e_souk.Dto.Shop.ShopFilterDto;
import com.example.e_souk.Dto.Shop.ShopResponseDTO;
import com.example.e_souk.Dto.Shop.ShopSummaryDTO;
import com.example.e_souk.Dto.Shop.UpdateShopRequestDTO;
import com.example.e_souk.Exception.ShopException;
import com.example.e_souk.Mappers.ProductMapper;
import com.example.e_souk.Mappers.ShopMapper;
import com.example.e_souk.Model.Category;
import com.example.e_souk.Model.Product;
import com.example.e_souk.Model.Role;
import com.example.e_souk.Model.Shop;
import com.example.e_souk.Model.User;
import com.example.e_souk.Repository.CategoryRepository;
import com.example.e_souk.Repository.ReviewRepository;
import com.example.e_souk.Repository.ShopRepository;
import com.example.e_souk.Repository.UserRepository;
import java.io.IOException; 

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service pour gérer la logique métier des boutiques
 * 
 * RESPONSABILITÉS PRINCIPALES :
 * 1. Créer une boutique ET changer le rôle CLIENT → VENDOR
 * 2. Gérer les boutiques (CRUD)
 * 3. Calculer les statistiques
 * 4. Appliquer les règles métier
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService; 
    private final CategoryService categoryService; 
    private final ReviewRepository reviewRepository; 

    // ShopFollowerRepository sera injecté quand il sera créé
    // private final ShopFollowerRepository shopFollowerRepository;

    /**
     * Crée une nouvelle boutique pour un utilisateur
     *      * 
     * @param requestDTO données de la boutique
     * @param ownerId ID de l'utilisateur (récupéré du JWT)
     * @return ShopResponseDTO la boutique créée
     */
    @Transactional
    public ShopResponseDTO createShop(CreateShopRequestDTO requestDTO, UUID ownerId) throws java.io.IOException {
        log.info("Tentative de création de boutique '{}' pour l'utilisateur ID: {}", 
                requestDTO.getBrandName(), ownerId);
        
        // ÉTAPE 1 : Récupérer l'utilisateur
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> {
                    log.error("Utilisateur non trouvé avec l'ID: {}", ownerId);
                    return new ShopException("SHOP_ERROR", "Utilisateur non trouvé");
                });
        // ÉTAPE 2 : Vérifier que l'utilisateur n'a pas déjà une boutique active
        if (shopRepository.existsActiveShopByOwnerId(ownerId)) {
            log.warn("L'utilisateur {} a déjà une boutique active", owner.getUsername());
            throw new ShopException("SHOP_ERROR", "Vous avez déjà une boutique active. Un utilisateur ne peut avoir qu'une seule boutique active.");
        }
        // ÉTAPE 3 : Vérifier que le nom de marque n'existe pas déjà
        Optional<Shop> existingShop = shopRepository.findByBrandNameIgnoreCase(requestDTO.getBrandName());
        if (existingShop.isPresent()) {
            log.warn("Tentative de création d'une boutique avec un nom déjà existant: {}", requestDTO.getBrandName());
            throw new ShopException("SHOP_ERROR", "Une boutique avec ce nom existe déjà : " + requestDTO.getBrandName());
        }
        String savedFileName = null;
        MultipartFile logoPicture = requestDTO.getLogoPicture();
        if (logoPicture != null && !logoPicture.isEmpty()) {
             try {  savedFileName = fileStorageService.storeFile(logoPicture);
                log.info("Logo de la boutique enregistrée: {}", savedFileName); }
     catch (IOException e) {
                log.error("Erreur lors de l'enregistrement du logo de la boutique", e);
                throw new RuntimeException("Erreur lors de l'enregistrement du logo de la boutique.");
            } catch (IllegalArgumentException e) {
                log.warn("Type de fichier invalide pour le logo de la boutique: {}", e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
        }
   // ÉTAPE 3.5 : Gestion de la catégorie
    Category category = categoryRepository.findByNameIgnoreCase(requestDTO.getCategoryName())
            .orElseGet(() -> {
                try {
                    log.info("Catégorie '{}' non trouvée, création d'une nouvelle catégorie", requestDTO.getCategoryName());
                    CategoryRequestDTO newCategoryDTO = new CategoryRequestDTO();
                    newCategoryDTO.setName(requestDTO.getCategoryName());
                    newCategoryDTO.setDescription("Créé automatiquement lors de la création de boutique");
                    CategoryResponseDTO categoryResponse = categoryService.createCategory(newCategoryDTO);
                    
                    // Récupération de l'entité correspondante
                    return categoryRepository.findByNameIgnoreCase(categoryResponse.getName())
                            .orElseThrow(() -> new RuntimeException("Échec de création de la catégorie"));
                            
                } catch (Exception e) {
                    // Si la création échoue (par exemple à cause d'un nom dupliqué), 
                    // on essaie de récupérer la catégorie existante
                    log.warn("Échec de création de catégorie '{}', tentative de récupération: {}", 
                            requestDTO.getCategoryName(), e.getMessage());
                    
                    return categoryRepository.findByNameIgnoreCase(requestDTO.getCategoryName())
                            .orElseThrow(() -> new RuntimeException("Impossible de créer ou récupérer la catégorie: " + requestDTO.getCategoryName()));
                }
            });
        // ÉTAPE 4 : Créer la boutique
        Shop shop = new Shop();
        shop.setBrandName(requestDTO.getBrandName().trim());
        shop.setDescription(requestDTO.getDescription() != null ? requestDTO.getDescription().trim() : null);
        shop.setDeliveryFee(requestDTO.getDeliveryFee());
        shop.setAddress(requestDTO.getAddress().trim());
        shop.setIsActive(true); // Nouvelle boutique = active par défaut
        shop.setCreatedAt(LocalDateTime.now());
        shop.setCategoryName(category.getName());
        shop.setBio(requestDTO.getBio());
        shop.setPhone(requestDTO.getPhone());
        shop.setFacebookLink(requestDTO.getFacebookLink());
        shop.setInstagramLink(requestDTO.getInstagramLink());
        shop.setUpdatedAt(LocalDateTime.now());
        shop.setLogoPicture(savedFileName);
        shop.setOwner(owner);
        // ÉTAPE 5 : Sauvegarder la boutique
        Shop savedShop = shopRepository.save(shop);
        log.info("Boutique créée avec succès - ID: {}", savedShop.getId());
        // ÉTAPE 6 : CHANGER LE RÔLE CLIENT → VENDOR (RÈGLE MÉTIER IMPORTANTE!)
        if (owner.getRole() == Role.CLIENT) {
            owner.setRole(Role.VENDOR);
            owner.setUpdatedAt(LocalDateTime.now());
            userRepository.save(owner);
            log.info("Rôle de l'utilisateur {} changé de CLIENT à VENDOR", owner.getUsername());
        }
        // ÉTAPE 7 : Retourner le DTO complet
    long productCount = shopRepository.countProductsInShop(savedShop.getId());
    long orderCount = shopRepository.countOrdersInShop(savedShop.getId());
    long followerCount = 0;
    return ShopMapper.toResponseDTO(savedShop, productCount, orderCount, followerCount);
    }
   
    /**
     * Met à jour une boutique existante
     * SÉCURITÉ : Seul le propriétaire peut modifier sa boutique
     * @param shopId ID de la boutique
     * @param requestDTO nouvelles données
     * @param ownerId ID du propriétaire (pour vérification)
     * @return ShopResponseDTO boutique mise à jour
     */
    @Transactional
    public ShopResponseDTO updateShop(UUID shopId, UpdateShopRequestDTO requestDTO, UUID ownerId) {
        log.info("Mise à jour de la boutique ID: {} par l'utilisateur ID: {}", shopId, ownerId);
        
        // RÉCUPÉRATION et vérification de propriété
    Shop existingShop = shopRepository.findById(shopId)
        .orElseThrow(() -> new ShopException("SHOP_ERROR", "Boutique non trouvée"));
        
    if (!existingShop.getOwner().getId().equals(ownerId)) {
        log.warn("Tentative de modification non autorisée de la boutique {} par l'utilisateur {}", 
            shopId, ownerId);
        throw new ShopException("SHOP_ERROR", "Vous ne pouvez modifier que votre propre boutique");
    }
        
        // MISE À JOUR des champs fournis (pattern "update partiel")
        if (requestDTO.getBrandName() != null && !requestDTO.getBrandName().trim().isEmpty()) {
            // Vérifier que le nouveau nom n'existe pas déjà (sauf pour cette boutique)
            Optional<Shop> shopWithSameName = shopRepository.findByBrandNameIgnoreCase(requestDTO.getBrandName());
            if (shopWithSameName.isPresent() && !shopWithSameName.get().getId().equals(shopId)) {
                throw new ShopException("SHOP_ERROR", "Une autre boutique avec ce nom existe déjà");
            }
            existingShop.setBrandName(requestDTO.getBrandName().trim());
        }
        
        if (requestDTO.getDescription() != null) {
            existingShop.setDescription(requestDTO.getDescription().trim());
        }
        
        if (requestDTO.getDeliveryFee() != null) {
            existingShop.setDeliveryFee(requestDTO.getDeliveryFee());
        }
        
        if (requestDTO.getAddress() != null && !requestDTO.getAddress().trim().isEmpty()) {
            existingShop.setAddress(requestDTO.getAddress().trim());
        }
        
        if (requestDTO.getIsActive() != null) {
            existingShop.setIsActive(requestDTO.getIsActive());
        }
        
        existingShop.setUpdatedAt(LocalDateTime.now());
        
        // SAUVEGARDE
        Shop updatedShop = shopRepository.save(existingShop);
        log.info("Boutique mise à jour avec succès - ID: {}", updatedShop.getId());
        
    long productCount = shopRepository.countProductsInShop(updatedShop.getId());
    long orderCount = shopRepository.countOrdersInShop(updatedShop.getId());
    long followerCount = 0;
    return ShopMapper.toResponseDTO(updatedShop, productCount, orderCount, followerCount);
    }

    /**
     * Récupère la boutique d'un utilisateur
     * 
     * @param ownerId ID du propriétaire
     * @return ShopResponseDTO la boutique de l'utilisateur
     */
    @Transactional(readOnly = true)
    public ShopResponseDTO getShopByOwnerId(UUID ownerId) {
        log.info("Récupération de la boutique de l'utilisateur ID: {}", ownerId);
        
    Shop shop = shopRepository.findActiveShopByOwnerId(ownerId)
        .orElseThrow(() -> new ShopException("SHOP_ERROR", "Aucune boutique active trouvée pour cet utilisateur"));
        
    long productCount = shopRepository.countProductsInShop(shop.getId());
    long orderCount = shopRepository.countOrdersInShop(shop.getId());
    long followerCount = 0;
    return ShopMapper.toResponseDTO(shop, productCount, orderCount, followerCount);
    }

    /**
     * Récupère toutes les boutiques actives avec pagination
     * 
     * @param pageable pagination
     * @return Page<ShopSummaryDTO> page de boutiques
     */
    @Transactional(readOnly = true)
    public Page<ShopSummaryDTO> getAllActiveShops(Pageable pageable) {
        log.info("Récupération des boutiques actives - Page: {}, Size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Shop> shops = shopRepository.findAllActiveShops(pageable);
        
    return shops.map(shop -> ShopMapper.toSummaryDTO(shop, shopRepository.countProductsInShop(shop.getId()), 0));
    }
// recuperer les produits + FILTRAGE 
  public Page<ShopSummaryDTO> findShops(ShopFilterDto filters) { 
        // Création du tri
        Sort sort = switch (filters.getSortBy().toLowerCase()) {
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            // case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            // case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            // case "name_asc" -> Sort.by(Sort.Direction.ASC, "name");
            // case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.by(Sort.Direction.DESC, "createdAt"); // newest par défaut
        };
        
        Pageable pageable = PageRequest.of(filters.getPage(), filters.getPageSize(), sort);
        
        // Récupérer la page de Product
    Page<Shop> shopPage = shopRepository.findshopsWithFilters(
        filters.getCategoryName(),
        filters.getAddress(),
        filters.getSearchKeyword(),
        pageable
    );

    // Convertir en Page<ShopSummaryDTO>
    return shopPage.map(shop -> ShopMapper.toSummaryDTO(shop, shopRepository.countProductsInShop(shop.getId()), 0));
    }
}

