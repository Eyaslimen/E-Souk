import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Shop } from '../interfaces/Shop';
import { ProductDetails, Review, ReviewRequest, ShopGeneralDetails } from '../interfaces/ProductDetails';
import { UserProfile } from '../interfaces/UserProfile';
import { catchError, Observable, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ShopDetails {
  private readonly API_URL = 'http://localhost:8080/api/shops';
  constructor(private http: HttpClient) {}
 getShopDetails(name: string): Observable<ShopGeneralDetails> {
    console.log('Fetching shop:', name);
    return this.http.get<ShopGeneralDetails>(`${this.API_URL}/${name}`)
      .pipe(
        catchError(this.handleError)
      );
  }
  
 getShopProducts(name: string): Observable<ProductDetails[]> {
    console.log('Fetching shop products for:', name);
    return this.http.get<ProductDetails[]>(`${this.API_URL}/${name}/products`)
      .pipe(
        catchError(this.handleError)
      );
  }

addReview(request: ReviewRequest, shopName: string): Observable<Review> {
    console.log('Adding review for shop:', shopName, 'with data:', request);
    console.log('Full URL:', `${this.API_URL}/${shopName}/review`);
    
    return this.http.post<Review>(`${this.API_URL}/${shopName}/review`, request)
        .pipe(
            tap((response: any) => console.log('Review created successfully:', response)),
            catchError(error => {
                console.error('Error creating review:', error);
                console.error('Error status:', error.status);
                console.error('Error message:', error.message);
                return this.handleError(error);
            })
        );
}

 getShopReviews(name: string): Observable<Review[]> {
    console.log('Fetching shop reviews for:', name);
    return this.http.get<Review[]>(`${this.API_URL}/${name}/reviews`)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Erreur HTTP:', error);
    if (error.status === 404) {
      return throwError(() => new Error('non trouvée'));
    }
    return throwError(() => new Error('Erreur serveur'));
  }

}
