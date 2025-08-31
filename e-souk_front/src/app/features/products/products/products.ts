import { Component, type OnInit } from "@angular/core";
import { ProductDetails } from "../../../interfaces/ProductDetails";
import { ProductCard } from '../../../shared/product-card/product-card';
import { ProductCarousel } from "../product-carousel/product-carousel";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Observable, BehaviorSubject, combineLatest, debounceTime, distinctUntilChanged } from "rxjs";
import { ProductsService, ProductFilters, PagedResponse } from "../../../services/products-service";
import { map, switchMap, startWith } from 'rxjs/operators';
import { ProductsFavoriteService } from "../../../services/products-favorite.service";

interface SortOption {
  value: string;
  label: string;
}

@Component({
  standalone: true,
  selector: 'app-products',
  imports: [ProductCard, CommonModule, FormsModule,ProductCarousel],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products implements OnInit {
  // Propriétés pour la pagination et les données
  pagedProducts$!: Observable<PagedResponse<ProductDetails>>;
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;

  // Propriétés pour les filtres
  searchQuery = '';
  selectedCategories: string[] = [];
  minPrice = '';
  maxPrice = '';
  sortBy = 'newest';
  
  // Propriétés pour l'UI
  showCategoryDropdown = false;
  showSortDropdown = false;
  loading = false;

  // Subjects pour la réactivité
  private filtersSubject = new BehaviorSubject<ProductFilters>({
    page: 0,
    pageSize: 20,
    sortBy: 'newest'
  });

  // Catégories exactes de votre backend
  categories: string[] = [
    'vêtements',
    'accessoires', 
    'maison', 
    'sport', 
    'technologie'
  ];

  sortOptions: SortOption[] = [
    { value: 'newest', label: 'Plus récent' },
    { value: 'oldest', label: 'Plus ancien' }
  ];



  constructor(private productService: ProductsService, private productsFavoriteService: ProductsFavoriteService) {}

  ngOnInit(): void {
    // Configuration de l'observable principal pour les produits avec pagination
    this.pagedProducts$ = this.filtersSubject.pipe(
      debounceTime(300), // Délai pour éviter trop de requêtes
      distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)),
      switchMap(filters => {
        this.loading = true;
        return this.productService.getProducts(filters);
      }),
      map(response => {
        this.loading = false;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.currentPage = response.number;
        return response;
      })
    );

    // Chargement initial
    this.applyFilters();
  }

  // Méthodes pour les dropdowns
  toggleCategoryDropdown(): void {
    this.showCategoryDropdown = !this.showCategoryDropdown;
    this.showSortDropdown = false;
  }

  toggleSortDropdown(): void {
    this.showSortDropdown = !this.showSortDropdown;
    this.showCategoryDropdown = false;
  }

  // Méthodes pour la gestion des catégories (sélection unique)
  selectSingleCategory(category: string): void {
    this.selectedCategories = [category];
    this.showCategoryDropdown = false;
    this.applyFilters();
  }

  clearCategorySelection(): void {
    this.selectedCategories = [];
    this.showCategoryDropdown = false;
    this.applyFilters();
  }

  // Ancienne méthode gardée pour compatibilité mais adaptée
  toggleCategory(category: string): void {
    this.selectSingleCategory(category);
  }

  removeCategory(category: string): void {
    this.clearCategorySelection();
  }

  // Méthodes pour le tri
  setSortBy(value: string): void {
    this.sortBy = value;
    this.showSortDropdown = false;
    this.applyFilters();
  }

  getSortLabel(value: string): string {
    const option = this.sortOptions.find(opt => opt.value === value);
    return option ? option.label : 'Plus récent';
  }

  // Méthode pour effacer tous les filtres
  clearAllFilters(): void {
    this.searchQuery = '';
    this.selectedCategories = [];
    this.minPrice = '';
    this.maxPrice = '';
    this.sortBy = 'newest';
    this.currentPage = 0;
    this.applyFilters();
  }

  // Vérification s'il y a des filtres actifs
  hasActiveFilters(): boolean {
    return !!(this.searchQuery || 
             this.selectedCategories.length > 0 || 
             this.minPrice || 
             this.maxPrice || 
             this.sortBy !== 'newest');
  }

  // Application des filtres
  applyFilters(): void {
    const filters: ProductFilters = {
      page: this.currentPage,
      pageSize: this.pageSize,
      sortBy: this.sortBy
    };

    // Ajout des filtres conditionnels
    if (this.searchQuery.trim()) {
      filters.searchKeyword = this.searchQuery.trim();
    }

    if (this.selectedCategories.length > 0) {
      // Si vous voulez supporter plusieurs catégories, vous devrez modifier votre backend
      // Pour l'instant, on prend la première catégorie sélectionnée
      filters.categoryName = this.selectedCategories[0];
    }

    if (this.minPrice && !isNaN(parseFloat(this.minPrice))) {
      filters.priceMin = parseFloat(this.minPrice);
    }

    if (this.maxPrice && !isNaN(parseFloat(this.maxPrice))) {
      filters.priceMax = parseFloat(this.maxPrice);
    }

    // Reset de la page lors de l'application de nouveaux filtres
    this.currentPage = 0;
    filters.page = 0;

    this.filtersSubject.next(filters);
  }

  // Méthodes pour la pagination
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      const currentFilters = this.filtersSubject.value;
      this.filtersSubject.next({
        ...currentFilters,
        page: page
      });
    }
  }

  nextPage(): void {
    this.goToPage(this.currentPage + 1);
  }

  previousPage(): void {
    this.goToPage(this.currentPage - 1);
  }

  // Méthodes utilitaires pour la pagination
  get pageNumbers(): number[] {
    const pages = [];
    const start = Math.max(0, this.currentPage - 2);
    const end = Math.min(this.totalPages - 1, this.currentPage + 2);
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  // Méthode pour obtenir les informations de pagination
  getPaginationInfo(): string {
    const startItem = this.currentPage * this.pageSize + 1;
    const endItem = Math.min((this.currentPage + 1) * this.pageSize, this.totalElements);
    return `${startItem}-${endItem} sur ${this.totalElements}`;
  }
// Ajoutez cette méthode dans votre composant pour optimiser les performances
trackByProductId(index: number, product: any): string {
  return `${product.id}-${index}`;
}

}