import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProductDetails } from '../interfaces/ProductDetails';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Shop } from '../interfaces/Shop';
import { ShopSummaryDTO } from '../interfaces/user-info';

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
export class ShopFollowrsService {
  private readonly API_URL = 'http://localhost:8080/api/shop-followers';

  constructor(private http: HttpClient) {} 


  followShop(shopId:any) : Observable<ShopSummaryDTO> {
    return this.http.post<ShopSummaryDTO>(`${this.API_URL}/follow/${shopId}`, {})  
        .pipe(
          tap(response => console.log('Follow shop response:', response)),
          catchError(this.handleError)
        );
  }

  unFollowShop(shopId:any) : Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/unfollow/${shopId}`);
  }
    
  
  private handleError(error: HttpErrorResponse): Observable<never> {
      console.error('API Error:', error);
      let errorMessage = 'Une erreur est survenue';
      
      if (error.error instanceof ErrorEvent) {
        // Erreur côté client
        errorMessage = `Erreur: ${error.error.message}`;
      } else {
        // Erreur côté serveur
        errorMessage = `Code d'erreur: ${error.status}, Message: ${error.message}`;
      }
      
      return throwError(() => new Error(errorMessage));
    }

}
