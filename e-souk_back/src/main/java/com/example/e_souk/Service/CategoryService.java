package com.example.e_souk.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_souk.Dto.Category.CategoryRequestDTO;
import com.example.e_souk.Dto.Category.CategoryResponseDTO;
import com.example.e_souk.Dto.Category.CategorySummaryDTO;
import com.example.e_souk.Mappers.CategoryMapper;
import com.example.e_souk.Model.Category;
import com.example.e_souk.Repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour gérer la logique métier des catégories
 * 
 * POURQUOI @RequiredArgsConstructor ?
 * - Lombok génère automatiquement un constructeur avec les champs finals
 * - Plus propre que @Autowired
 * - Injection de dépendance par constructeur (recommandée)
 * 
 * POURQUOI @Slf4j ?
 * - Lombok génère automatiquement un logger
 * - On peut faire log.info(), log.error(), etc.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Crée une nouvelle catégorie
     * 
     * @param requestDTO données de la catégorie à créer
     * @return CategoryResponseDTO la catégorie créée
     * @throws IllegalArgumentException si une catégorie avec ce nom existe déjà
     */
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        log.info("Tentative de création d'une catégorie avec le nom : {}", requestDTO.getName());
        // VÉRIFICATION 1 : Est-ce que cette catégorie existe déjà ?
        Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(requestDTO.getName());
        if (existingCategory.isPresent()) {
            log.warn("Tentative de création d'une catégorie déjà existante : {}", requestDTO.getName());
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà : " + requestDTO.getName());
        }
        
        // CRÉATION de l'entité Category
        Category category = new Category();
        category.setName(requestDTO.getName().trim()); // trim() enlève les espaces avant/après
        category.setDescription(requestDTO.getDescription() != null ? requestDTO.getDescription().trim() : null);
        category.setCreatedAt(LocalDateTime.now());
        
        // SAUVEGARDE en base
        Category savedCategory = categoryRepository.save(category);
        log.info("Catégorie créée avec succès - ID : {}, Nom : {}", savedCategory.getId(), savedCategory.getName());
        
        // CONVERSION en DTO pour le retour
        return convertToResponseDTO(savedCategory);
    }

    /**
     * Récupère toutes les catégories avec pagination
     * 
     * POURQUOI la pagination ?
     * - Performance : évite de charger 10 000 catégories d'un coup
     * - UX : permet de naviguer par pages côté frontend
     * 
     * @param pageable informations de pagination (page, taille, tri)
     * @return Page<CategoryResponseDTO> page de catégories
     */
    @Transactional(readOnly = true) // readOnly = true pour optimiser les lectures
    public Page<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        log.info("Récupération des catégories - Page : {}, Taille : {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Category> categories = categoryRepository.findAll(pageable);
        
        // CONVERSION de chaque Category en CategoryResponseDTO
        return categories.map(this::convertToResponseDTO);
    }

    /**
     * Récupère une catégorie par son ID
     * 
     * @param id ID de la catégorie
     * @return CategoryResponseDTO la catégorie trouvée
     * @throws IllegalArgumentException si la catégorie n'existe pas
     */
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(UUID id) {
        log.info("Recherche de la catégorie avec l'ID : {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Catégorie non trouvée avec l'ID : {}", id);
                    return new IllegalArgumentException("Catégorie non trouvée avec l'ID : " + id);
                });
        
        return convertToResponseDTO(category);
    }

    /**
     * Met à jour une catégorie existante
     * 
     * @param id ID de la catégorie à modifier
     * @param requestDTO nouvelles données
     * @return CategoryResponseDTO la catégorie mise à jour
     */
    @Transactional
    public CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO requestDTO) {
        log.info("Mise à jour de la catégorie ID : {} avec le nom : {}", id, requestDTO.getName());
        
        // RÉCUPÉRATION de la catégorie existante
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec l'ID : " + id));
        
        // VÉRIFICATION : est-ce qu'une autre catégorie a déjà ce nom ?
        Optional<Category> categoryWithSameName = categoryRepository.findByNameIgnoreCase(requestDTO.getName());
        if (categoryWithSameName.isPresent() && !categoryWithSameName.get().getId().equals(id)) {
            throw new IllegalArgumentException("Une autre catégorie avec ce nom existe déjà : " + requestDTO.getName());
        }
        
        // MISE À JOUR des champs
        existingCategory.setName(requestDTO.getName().trim());
        existingCategory.setDescription(requestDTO.getDescription() != null ? requestDTO.getDescription().trim() : null);  
        // SAUVEGARDE
        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Catégorie mise à jour avec succès - ID : {}", updatedCategory.getId());

        return categoryMapper.convertToResponseDTO(updatedCategory);
    }

    /**
     * Supprime une catégorie
     * 
     * RÈGLE MÉTIER : On ne peut pas supprimer une catégorie qui a des produits
     * 
     * @param id ID de la catégorie à supprimer
     * @throws IllegalArgumentException si la catégorie a des produits
     */
    @Transactional
    public void deleteCategory(UUID id) {
        log.info("Tentative de suppression de la catégorie ID : {}", id);
        
        // VÉRIFICATION 1 : est-ce que la catégorie existe ?
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Catégorie non trouvée avec l'ID : " + id);
        }
        
        // VÉRIFICATION 2 : est-ce que la catégorie a des produits ?
        long productCount = categoryRepository.countProductsInCategory(id);
        if (productCount > 0) {
            log.warn("Tentative de suppression d'une catégorie contenant {} produits", productCount);
            throw new IllegalArgumentException("Impossible de supprimer cette catégorie car elle contient " + productCount + " produit(s)");
        }
        
        // SUPPRESSION
        categoryRepository.deleteById(id);
        log.info("Catégorie supprimée avec succès - ID : {}", id);
    }

    /**
     * Recherche des catégories par nom (pour l'auto-complétion)
     * 
     * @param searchTerm terme de recherche
     * @return List<CategorySummaryDTO> catégories correspondantes
     */
    @Transactional(readOnly = true)
    public List<CategorySummaryDTO> searchCategories(String searchTerm) {
        log.info("Recherche de catégories avec le terme : {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of(); // Retourne une liste vide
        }
        
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(searchTerm.trim());
        
        // CONVERSION en DTOs légers
        return categories.stream()
                .map(category -> new CategorySummaryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Récupère toutes les catégories au format simplifié (pour les listes déroulantes)
     * 
     * @return List<CategorySummaryDTO> toutes les catégories simplifiées
     */
    @Transactional(readOnly = true)
    public List<CategorySummaryDTO> getAllCategoriesSummary() {
        log.info("Récupération de toutes les catégories au format simplifié");
        
        List<Category> categories = categoryRepository.findAll();
        
        return categories.stream()
                .map(category -> new CategorySummaryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }

    // Ajout de la méthode manquante convertToResponseDTO
    private CategoryResponseDTO convertToResponseDTO(Category category) {
        return categoryMapper.convertToResponseDTO(category);
    }
}