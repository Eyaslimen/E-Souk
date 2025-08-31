import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProductDetails } from '../interfaces/ProductDetails';
import { catchError, Observable, throwError } from 'rxjs';

// Interface pour les filtres (alignée avec votre backend)
export interface ProductFilters {
  categoryName?: string;
  priceMin?: number;
  priceMax?: number;
  searchKeyword?: string;
  sortBy?: string;
  page?: number;
  pageSize?: number;
}

// Interface pour la réponse paginée (alignée avec Spring Boot Page)
export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  private readonly API_URL = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  // Méthode pour récupérer tous les produits sans pagination (gardée pour compatibilité)
  getAllProducts(): Observable<ProductDetails[]> {
    return this.http.get<ProductDetails[]>(`${this.API_URL}/all`)
      .pipe(
        catchError(this.handleError)
      );
  }
  getProductDetails(productId:any): Observable<ProductDetailsDto> {
    return this.http.get<ProductDetailsDto>(`${this.API_URL}/${productId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Nouvelle méthode pour récupérer les produits avec filtrage et pagination
  getProducts(filters: ProductFilters = {}): Observable<PagedResponse<ProductDetails>> {
    let params = new HttpParams();

    // Ajout des paramètres de filtrage s'ils sont définis
    if (filters.categoryName) {
      params = params.set('categoryName', filters.categoryName);
    }
    if (filters.priceMin !== undefined && filters.priceMin !== null) {
      params = params.set('priceMin', filters.priceMin.toString());
    }
    if (filters.priceMax !== undefined && filters.priceMax !== null) {
      params = params.set('priceMax', filters.priceMax.toString());
    }
    if (filters.searchKeyword) {
      params = params.set('searchKeyword', filters.searchKeyword);
    }
    if (filters.sortBy) {
      params = params.set('sortBy', filters.sortBy);
    }
    if (filters.page !== undefined) {
      params = params.set('page', filters.page.toString());
    }
    if (filters.pageSize !== undefined) {
      params = params.set('pageSize', filters.pageSize.toString());
    }

    return this.http.get<PagedResponse<ProductDetails>>(this.API_URL, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Erreur HTTP:', error);
    if (error.status === 404) {
      return throwError(() => new Error('Produits non trouvés'));
    }
    return throwError(() => new Error('Erreur serveur'));
  }
}