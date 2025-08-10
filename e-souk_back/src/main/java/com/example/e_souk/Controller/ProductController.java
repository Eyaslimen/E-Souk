package com.example.e_souk.Controller;

import java.util.List;
import java.util.UUID;
import com.example.e_souk.Dto.ProductCreationRequestDTO;
import com.example.e_souk.Dto.ProductDetailsDTO;
import com.example.e_souk.Model.Product;
import com.example.e_souk.Dto.ProductResponseDTO;
import com.example.e_souk.Service.ProductService;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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
            ProductResponseDTO responseDTO = productService.toProductResponseDTO(product);
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
}