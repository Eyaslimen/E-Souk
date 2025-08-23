import { Component, Input, OnInit } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Product } from '../../../interfaces/shopDetails';
import { ProductsService } from '../../../services/shopDetails/products.service';
import { CommonModule } from '@angular/common';
import { ShopDetails } from '../../../services/shop-details';
import { ProductDetails } from '../../../interfaces/ProductDetails';
import { ProductCard } from '../../../shared/product-card/product-card';

@Component({
  selector: 'app-products',
  imports: [CommonModule, ProductCard],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products implements OnInit {
  allProducts$!: Observable<Product[]>
  products$!: Observable<ProductDetails[]>
  filteredProducts$!: Observable<Product[]>
  categories: string[] = []

  // Filters
  searchTerm$ = new BehaviorSubject<string>("")
  selectedCategory$ = new BehaviorSubject<string>("")
  selectedPriceRange$ = new BehaviorSubject<string>("")
  selectedSort$ = new BehaviorSubject<string>("newest")
  @Input() name!: string;

  // View mode
  viewMode: "grid" | "list" = "grid"

  constructor(private productsService: ProductsService, private shopDetails:ShopDetails) {}

  ngOnInit(): void {
    this.allProducts$ = this.productsService.getAllProducts()
    this.categories = this.productsService.getCategories()
    this.products$ = this.shopDetails.getShopProducts(this.name);
    // Combine all filters
    this.filteredProducts$ = combineLatest([
      this.allProducts$,
      this.searchTerm$,
      this.selectedCategory$,
      this.selectedPriceRange$,
      this.selectedSort$
    ]).pipe(
      map(([products, searchTerm, category, priceRange, sort]: [Product[], string, string, string, string]) => {
        let filtered = [...products]

        // Search filter
        if (searchTerm) {
          filtered = filtered.filter((p) => p.name.toLowerCase().includes(searchTerm.toLowerCase()))
        }

        // Category filter
        if (category) {
          filtered = filtered.filter((p) => p.category === category)
        }

        // Price range filter
        if (priceRange) {
          const [min, max] = priceRange.split("-").map(Number)
          filtered = filtered.filter((p) => {
            if (max) {
              return p.price >= min && p.price <= max
            } else {
              return p.price >= min
            }
          })
        }

        // Sort
        switch (sort) {
          case "newest":
            filtered.sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime())
            break
          case "oldest":
            filtered.sort((a, b) => a.createdAt.getTime() - b.createdAt.getTime())
            break
          case "price-low":
            filtered.sort((a, b) => a.price - b.price)
            break
          case "price-high":
            filtered.sort((a, b) => b.price - a.price)
            break
          case "rating":
            filtered.sort((a, b) => b.rating - a.rating)
            break
        }

        return filtered
      })
    )
  }

  onSearchChange(term: string): void {
    this.searchTerm$.next(term)
  }

  onCategoryChange(category: string): void {
    this.selectedCategory$.next(category)
  }

  onPriceRangeChange(range: string): void {
    this.selectedPriceRange$.next(range)
  }

  onSortChange(sort: string): void {
    this.selectedSort$.next(sort)
  }

  resetFilters(): void {
    this.searchTerm$.next("")
    this.selectedCategory$.next("")
    this.selectedPriceRange$.next("")
    this.selectedSort$.next("newest")
  }

  setViewMode(mode: "grid" | "list"): void {
    this.viewMode = mode
  }

  getStarArray(rating: number): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => (i < rating ? 1 : 0))
  }
}
