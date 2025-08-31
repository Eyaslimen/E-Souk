import { Component, type OnInit } from "@angular/core";
import { Shop } from "../../../interfaces/Shop";
import { ShopCard } from '../../../shared/shop-card/shop-card';
import { ShopsCarousel } from "../shops-carousel/shops-carousel";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { Observable, BehaviorSubject, debounceTime, distinctUntilChanged } from "rxjs";
import { ShopsService, ShopFilters, PagedResponse } from "../../../services/shops-service";
import { map, switchMap } from 'rxjs/operators';

interface SortOption {
  value: string;
  label: string;
}

@Component({
  standalone: true,
  selector: 'app-shops',
  imports: [ShopCard, CommonModule, FormsModule, ShopsCarousel],
  templateUrl: './shops.html',
  styleUrl: './shops.css'
})
export class Shops implements OnInit {
  // Propriétés pour la pagination et les données
  pagedShops$!: Observable<PagedResponse<Shop>>;
  currentPage = 0;
  pageSize = 20;
  totalElements = 0;
  totalPages = 0;

  // Propriétés pour les filtres
  searchQuery = '';
  selectedCategories: string[] = [];
  selectedAddress = '';
  sortBy = 'newest';
  
  // Propriétés pour l'UI
  showCategoryDropdown = false;
  showAddressDropdown = false;
  showSortDropdown = false;
  loading = false;

  // Subjects pour la réactivité
  private filtersSubject = new BehaviorSubject<ShopFilters>({
    page: 0,
    pageSize: 20,
    sortBy: 'newest'
  });

  // Catégories exactes de votre backend
  categories: string[] = [
    'Mode',
    'Technologie', 
    'Maison',
    'Sport',
    'Beauté',
    'Livre',
    'Jouet',
    'Alimentation',
    'Artisanat',
    'Autre'
  ];

  addresses: string[] = [
    'Tunis',
    'Sfax',
    'Sousse', 
    'Monastir',
    'Hammamet',
    'Nabeul',
    'Gabès',
    'Gafsa',
    'Kairouan',
    'Autre'
  ];

  sortOptions: SortOption[] = [
    { value: 'newest', label: 'Plus récent' },
    { value: 'oldest', label: 'Plus ancien' }
  ];

  constructor(private shopsService: ShopsService) {}

  ngOnInit(): void {
    // Configuration de l'observable principal pour les boutiques avec pagination
    this.pagedShops$ = this.filtersSubject.pipe(
      debounceTime(300), // Délai pour éviter trop de requêtes
      distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)),
      switchMap(filters => {
        this.loading = true;
        return this.shopsService.getShops(filters);
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
    this.showAddressDropdown = false;
    this.showSortDropdown = false;
  }

  toggleAddressDropdown(): void {
    this.showAddressDropdown = !this.showAddressDropdown;
    this.showCategoryDropdown = false;
    this.showSortDropdown = false;
  }

  toggleSortDropdown(): void {
    this.showSortDropdown = !this.showSortDropdown;
    this.showCategoryDropdown = false;
    this.showAddressDropdown = false;
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

  // Méthodes pour la gestion des adresses
  selectAddress(address: string): void {
    this.selectedAddress = address;
    this.showAddressDropdown = false;
    this.applyFilters();
  }

  clearAddressSelection(): void {
    this.selectedAddress = '';
    this.showAddressDropdown = false;
    this.applyFilters();
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
    this.selectedAddress = '';
    this.sortBy = 'newest';
    this.currentPage = 0;
    this.applyFilters();
  }

  // Vérification s'il y a des filtres actifs
  hasActiveFilters(): boolean {
    return !!(this.searchQuery || 
             this.selectedCategories.length > 0 || 
             this.selectedAddress || 
             this.sortBy !== 'newest');
  }

  // Application des filtres
  applyFilters(): void {
    const filters: ShopFilters = {
      page: this.currentPage,
      pageSize: this.pageSize,
      sortBy: this.sortBy
    };

    // Ajout des filtres conditionnels
    if (this.searchQuery.trim()) {
      filters.searchKeyword = this.searchQuery.trim();
    }

    if (this.selectedCategories.length > 0) {
      filters.categoryName = this.selectedCategories[0];
    }

    if (this.selectedAddress) {
      filters.address = this.selectedAddress;
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
  trackByShopId(index: number, shop: any): string {
    return `${shop.id}-${index}`;
  }
}
