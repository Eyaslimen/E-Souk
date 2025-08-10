package com.example.e_souk.Mappers;

import org.springframework.stereotype.Component;

import com.example.e_souk.Dto.CategoryResponseDTO;
import com.example.e_souk.Model.Category;
import com.example.e_souk.Repository.CategoryRepository;
@Component
public class CategoryMapper {
    private final CategoryRepository categoryRepository;

    public CategoryMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Convertit une entité Category en CategoryResponseDTO
     * 
     * POURQUOI cette méthode ?
     * - Évite la duplication de code
     * - Centralise la logique de conversion
     * - Plus facile à maintenir
     * 
     * @param category l'entité à convertir
     * @return CategoryResponseDTO le DTO converti
     */
    public CategoryResponseDTO convertToResponseDTO(Category category) {
        // Calcul du nombre de produits dans cette catégorie
        long productCount = categoryRepository.countProductsInCategory(category.getId());
        
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                productCount
        );
    }
}
