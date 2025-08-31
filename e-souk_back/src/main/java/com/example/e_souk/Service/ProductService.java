
package com.example.e_souk.Service;

import com.example.e_souk.Dto.Category.CategoryRequestDTO;
import com.example.e_souk.Dto.Category.CategoryResponseDTO;
import com.example.e_souk.Dto.Product.ProductCreationRequestDTO;
import com.example.e_souk.Dto.Product.ProductDTO;
import com.example.e_souk.Dto.Product.ProductDetailDTO;
import com.example.e_souk.Dto.Product.ProductFilterDTO;
import com.example.e_souk.Dto.Product.ProductResponseDTO;
import com.example.e_souk.Mappers.ProductMapper;
import com.example.e_souk.Model.Product;
import com.example.e_souk.Model.Shop;
import com.example.e_souk.Model.Category;
import com.example.e_souk.Model.Variant;
import com.example.e_souk.Model.Attribute;
import com.example.e_souk.Model.AttributeValue;
import com.example.e_souk.Repository.ProductRepository;
import com.example.e_souk.Repository.ShopRepository;
import com.example.e_souk.Repository.CategoryRepository;
import com.example.e_souk.Repository.VariantRepository;
import com.example.e_souk.Repository.AttributeRepository;
import com.example.e_souk.Repository.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Transactional
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

private final FileStorageService fileStorageService;
	private final ProductRepository productRepository;
	private final ShopRepository shopRepository;
	private final CategoryRepository categoryRepository;
	private final VariantRepository variantRepository;
	private final CategoryService categoryService;
	private final AttributeRepository attributeRepository;
	private final AttributeValueRepository attributeValueRepository;

	public Product createProduct(ProductCreationRequestDTO dto, UUID shopId) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new RuntimeException("Shop not found"));
		Category category = categoryRepository.findByNameIgnoreCase(dto.getCategoryName())
    							.orElseGet(() -> {
        	CategoryRequestDTO newCategoryDTO = new CategoryRequestDTO();
        	newCategoryDTO.setName(dto.getCategoryName());
        	newCategoryDTO.setDescription("créé par vendor");
        	CategoryResponseDTO categoryResponse = categoryService.createCategory(newCategoryDTO);
        	// Récupération de l'entité correspondante
        	return categoryRepository.findByNameIgnoreCase(categoryResponse.getName())
                .orElseThrow(() -> new RuntimeException("Category creation failed"));
    											});
        // Gérer l'upload de la photo de profil
        String savedFileName = null;
        MultipartFile profilePicture = dto.getImageUrl();
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                savedFileName = fileStorageService.storeFile(profilePicture);
                log.info("Photo de profil enregistrée pour l'utilisateur {}: {}", 
                        dto.getName(), savedFileName);
            } catch (IOException e) {
                log.error("Erreur lors de l'enregistrement de la photo de profil pour l'utilisateur {}", 
                        dto.getName(), e);
                throw new RuntimeException("Erreur lors de l'enregistrement de la photo de profil.");
            } catch (IllegalArgumentException e) {
                log.warn("Type de fichier invalide pour l'utilisateur {}: {}", 
                        dto.getName(), e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
        }
		Product product = Product.builder()
			.name(dto.getName())
			.description(dto.getDescription())
			.picture(savedFileName)
			.price(dto.getPrice())
			.shop(shop)
			.category(category) 
			.isActive(true)
			.build();
		product = productRepository.save(product);

		// Gestion des attributs (création/réutilisation)
		for (ProductCreationRequestDTO.AttributeDTO attrDTO : dto.getAttributes()) {
			Attribute attribute = attributeRepository.findByNameIgnoreCase(attrDTO.getName())
				.orElseGet(() -> {
					Attribute newAttr = new Attribute();
					newAttr.setName(attrDTO.getName());
					newAttr.setType("TEXT"); // valeur par défaut
					return attributeRepository.save(newAttr);
				});
			// Les valeurs sont gérées via AttributeValue plus bas
		}

		// Création des variantes et valeurs d'attributs
		for (ProductCreationRequestDTO.VariantDTO variantDTO : dto.getVariants()) {
			String sku = ("PROD-" + product.getId() + "-" + System.currentTimeMillis()).substring(0, 50);
			if (sku.length() < 3) sku = sku + "XXX";
			Variant variant = Variant.builder()
				.sku(sku)
				.stock(variantDTO.getStock())
				.isActive(true)
				.product(product)
				.build();
			variant = variantRepository.save(variant);

			for (ProductCreationRequestDTO.AttributeValueDTO attrValDTO : variantDTO.getAttributeValues()) {
				Attribute attribute = attributeRepository.findByNameIgnoreCase(attrValDTO.getAttributeName())
					.orElseThrow(() -> new RuntimeException("Attribute not found: " + attrValDTO.getAttributeName()));
				AttributeValue attributeValue = AttributeValue.builder()
					.value(attrValDTO.getValue())
					.variant(variant)
					.attribute(attribute)
					.build();
				attributeValueRepository.save(attributeValue);
			}
		}
		// Recharge le produit avec ses variantes pour le mapping DTO
		Product productWithVariants = productRepository.findById(product.getId())
			.orElseThrow(() -> new RuntimeException("Product not found after creation"));
		return productWithVariants;
	}

	// Récupère tous les produits
	public List<ProductDTO> getAllProducts() {
		// toProductDetails
		return productRepository.findAll().stream()
			.map(ProductMapper::toProductDetails)
			.collect(Collectors.toList());
	}

// 	public Product getProductById(UUID id) {
//     return productRepository.findById(id)
//         .orElseThrow(() -> new RuntimeException("Product not found"));
// }
// Dans votre ProductService.java

/**
 * Récupère les détails d'un produit avec toutes les options d'attributs disponibles
 * @param productId ID du produit
 * @return ProductDetailDTO avec toutes les informations nécessaires
 */
public ProductDetailDTO getProductDetail(UUID productId) {
    // 1. Récupérer le produit avec ses variantes et attributs
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
    
    // 2. Créer la map des attributs disponibles
    Map<String, List<String>> availableAttributes = new HashMap<>();
    
    // 3. Parcourir toutes les variantes actives du produit
    for (Variant variant : product.getVariants()) {
        if (variant.getIsActive()) {
            // Pour chaque variante active, parcourir ses attributs
            for (AttributeValue attributeValue : variant.getAttributeValues()) {
                String attributeName = attributeValue.getAttributeName();
                String attributeValueString = attributeValue.getValue();
                
                // Si l'attribut n'existe pas encore dans la map, l'ajouter
                if (!availableAttributes.containsKey(attributeName)) {
                    availableAttributes.put(attributeName, new ArrayList<>());
                }
                
                // Ajouter la valeur si elle n'existe pas déjà
                List<String> values = availableAttributes.get(attributeName);
                if (!values.contains(attributeValueString)) {
                    values.add(attributeValueString);
                }
            }
        }
    }
    // 4. Construire et retourner le DTO
    return ProductDetailDTO.builder()
            .productId(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .picture(product.getPicture())
            .shopName(product.getShop().getBrandName())
            .availableAttributes(availableAttributes)
            .build();
} 

// recuperer les produits + FILTRAGE 
  public Page<ProductDTO> findProducts(ProductFilterDTO filters) { 
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
    Page<Product> productPage = productRepository.findProductsWithFilters(
        filters.getCategoryName(),
        filters.getPriceMin(),
        filters.getPriceMax(),
        filters.getSearchKeyword(),
        pageable
    );
    
    // Convertir en Page<ProductDTO>
    return productPage.map(ProductMapper::toProductDetails);
    }
}

