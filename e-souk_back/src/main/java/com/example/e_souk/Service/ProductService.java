
package com.example.e_souk.Service;

import com.example.e_souk.Dto.ProductCreationRequestDTO;
import com.example.e_souk.Dto.ProductDetailsDTO;
import com.example.e_souk.Dto.ProductResponseDTO;
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
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
	public ProductResponseDTO toProductResponseDTO(Product product) {
		ProductResponseDTO dto = new ProductResponseDTO();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setPicture(product.getPicture());

		ProductResponseDTO.CategorySummaryDTO catDto = new ProductResponseDTO.CategorySummaryDTO();
		catDto.setId(product.getCategory().getId());
		catDto.setName(product.getCategory().getName());
		dto.setCategory(catDto);

		ProductResponseDTO.ShopSummaryDTO shopDto = new ProductResponseDTO.ShopSummaryDTO();
		shopDto.setId(product.getShop().getId());
		shopDto.setBrandName(product.getShop().getBrandName());
		dto.setShop(shopDto);

		if (product.getVariants() != null) {
			List<ProductResponseDTO.VariantSummaryDTO> variantDTOs = product.getVariants().stream().map(variant -> {
				ProductResponseDTO.VariantSummaryDTO vdto = new ProductResponseDTO.VariantSummaryDTO();
				vdto.setId(variant.getId());
				vdto.setSku(variant.getSku());
				vdto.setPrice(variant.getPrice());
				vdto.setStock(variant.getStock());
				if (variant.getAttributeValues() != null) {
					List<ProductResponseDTO.AttributeValueDTO> attrVals = variant.getAttributeValues().stream().map(av -> {
						ProductResponseDTO.AttributeValueDTO avdto = new ProductResponseDTO.AttributeValueDTO();
						avdto.setAttributeName(av.getAttribute().getName());
						avdto.setValue(av.getValue());
						return avdto;
					}).toList();
					vdto.setAttributeValues(attrVals);
				}
				return vdto;
			}).toList();
			dto.setVariants(variantDTOs);
		}
		return dto;
	}

private final FileStorageService fileStorageService;
	private final ProductRepository productRepository;
	private final ShopRepository shopRepository;
	private final CategoryRepository categoryRepository;
	private final VariantRepository variantRepository;
	private final AttributeRepository attributeRepository;
	private final AttributeValueRepository attributeValueRepository;

	public Product createProduct(ProductCreationRequestDTO dto, UUID shopId) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new RuntimeException("Shop not found"));
		Category category = categoryRepository.findById(dto.getCategoryId())
			.orElseThrow(() -> new RuntimeException("Category not found"));
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
				.price((float) variantDTO.getPrice())
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
	public List<ProductDetailsDTO> getAllProducts() {
		// toProductDetails
		return productRepository.findAll().stream()
			.map(ProductMapper::toProductDetails)
			.collect(Collectors.toList());
	}
//         Shop shop = shopRepository.findById(dto.getShopId())
//             .orElseThrow(() -> new RuntimeException("Shop not found"));
//         Category category = categoryRepository.findById(dto.getCategoryId())
//             .orElseThrow(() -> new RuntimeException("Category not found"));

//         Product product = Product.builder()
//             .name(dto.getName())
//             .description(dto.getDescription())
//             .pictures(dto.getPictures())
//             .shop(shop)
//             .category(category)
//             .isActive(true)
//             .build();
//         product = productRepository.save(product);

//         for (ProductCreationDTO.VariantDTO variantDTO : dto.getVariants()) {
//             String sku = "PROD-" + product.getId() + "-" + Instant.now().toEpochMilli();
//             Variant variant = Variant.builder()
//                 .sku(sku)
//                 .price(variantDTO.getPrice())
//                 .stock(variantDTO.getStock())
//                 .isActive(true)
//                 .product(product)
//                 .build();
//             variant = variantRepository.save(variant);

//             for (ProductCreationDTO.AttributeValueDTO attrDTO : variantDTO.getAttributes()) {
//                 Attribute attribute = attributeRepository.findById(attrDTO.getAttributeId())
//                     .orElseThrow(() -> new RuntimeException("Attribute not found"));
//                 AttributeValue attributeValue = AttributeValue.builder()
//                     .value(attrDTO.getValue())
//                     .variant(variant)
//                     .attribute(attribute)
//                     .build();
//                 attributeValueRepository.save(attributeValue);
//             }
//         }
//         return product;
//     }
}
