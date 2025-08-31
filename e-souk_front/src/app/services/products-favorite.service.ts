import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Shop } from '../interfaces/Shop';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { ProductDTO } from '../interfaces/user-info';

@Injectable({
  providedIn: 'root'
})
export class ProductsFavoriteService {
  private readonly API_URL = 'http://localhost:8080/api/products-favorites';
  
  constructor(private http: HttpClient) {}

 // Ajouter un produit aux favoris
  addProductToFavorite(productId: string): Observable<ProductDTO> {
    console.log('Adding to favorites with productId:', productId);
    return this.http.post<ProductDTO>(`${this.API_URL}/add/${productId}`, {})
      .pipe(
        tap(response => console.log('Add to favorites response:', response)),
        catchError(this.handleError)
      );
  }

  // Retirer un produit des favoris
  removeProductFromFavorite(productId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/remove/${productId}`);
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