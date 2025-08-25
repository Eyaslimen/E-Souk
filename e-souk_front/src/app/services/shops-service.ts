import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProductDetails } from '../interfaces/ProductDetails';
import { catchError, Observable, throwError } from 'rxjs';
import { Shop } from '../interfaces/Shop';

// Interface pour les filtres (alignée avec votre backend)
export interface ShopFilters {
  categoryName?: string;
  address?: string;
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
export class ShopsService {
  private readonly API_URL = 'http://localhost:8080/api/shops/all';

  constructor(private http: HttpClient) {}


  // Nouvelle méthode pour récupérer les boutiques avec filtrage et pagination
  getShops(filters: ShopFilters = {}): Observable<PagedResponse<Shop>> {
    let params = new HttpParams();

    // Ajout des paramètres de filtrage s'ils sont définis
    if (filters.categoryName) {
      params = params.set('categoryName', filters.categoryName);
    }
    if (filters.address) {
      params = params.set('address', filters.address);
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

    return this.http.get<PagedResponse<Shop>>(this.API_URL, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Erreur HTTP:', error);
    if (error.status === 404) {
      return throwError(() => new Error('Boutiques non trouvées'));
    }
    return throwError(() => new Error('Erreur serveur'));
  }
}