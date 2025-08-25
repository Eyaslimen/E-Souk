package com.example.e_souk.Controller;

import java.util.List;
import java.util.UUID;

import com.example.e_souk.Model.Product;
import com.example.e_souk.Dto.Product.ProductCreationRequestDTO;
import com.example.e_souk.Dto.Product.ProductDetailsDTO;
import com.example.e_souk.Dto.Product.ProductFilterDTO;
import com.example.e_souk.Dto.Product.ProductResponseDTO;
import com.example.e_souk.Mappers.ProductMapper;
import com.example.e_souk.Service.ProductService;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createProduct(@ModelAttribute ProductCreationRequestDTO dto, 
                                                          @RequestParam UUID shopId) {
        try {
            // Désérialiser les JSON strings en objets
            if (dto.getAttributesJson() != null && !dto.getAttributesJson().trim().isEmpty()) {
                List<ProductCreationRequestDTO.AttributeDTO> attributes = 
                    objectMapper.readValue(dto.getAttributesJson(), 
                        new TypeReference<List<ProductCreationRequestDTO.AttributeDTO>>() {});
                dto.setAttributes(attributes);
            }
            if (dto.getVariantsJson() != null && !dto.getVariantsJson().trim().isEmpty()) {
                List<ProductCreationRequestDTO.VariantDTO> variants = 
                    objectMapper.readValue(dto.getVariantsJson(), 
                        new TypeReference<List<ProductCreationRequestDTO.VariantDTO>>() {});
                dto.setVariants(variants);
            }
            
            Product product = productService.createProduct(dto, shopId);
            ProductResponseDTO responseDTO = ProductMapper.toProductResponseDTO(product);
            return ResponseEntity.ok(responseDTO);
            
        } catch (Exception e) {
            // Gérer les erreurs de désérialisation
            throw new RuntimeException("Erreur lors du parsing des données JSON: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<ProductDetailsDTO>> getAllProducts() {
        List<ProductDetailsDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDetailsDTO>> getProducts(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Float priceMin,
            @RequestParam(required = false) Float priceMax,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        
        ProductFilterDTO filters = new ProductFilterDTO();
        filters.setCategoryName(categoryName);
        filters.setPriceMin(priceMin);
        filters.setPriceMax(priceMax);
        filters.setSearchKeyword(searchKeyword);
        filters.setSortBy(sortBy);
        filters.setPage(page);
        filters.setPageSize(pageSize);
        
        Page<ProductDetailsDTO> products = productService.findProducts(filters);
        
        return ResponseEntity.ok(products);
    }
}