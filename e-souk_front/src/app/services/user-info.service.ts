import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { Cart, CartDto, ProductDTO, ShopSummaryDTO, UserOrdersDto, UserProfile } from '../interfaces/user-info';


@Injectable({
  providedIn: 'root'
})
export class UserInfoService {
  private readonly API_URL = 'http://localhost:8080/api/user-info';
  constructor(private http: HttpClient) { }

  // ==================== USER DETAILS ====================
getUserProfile(): Observable<UserProfile> {
  console.log('Appel API:', `${this.API_URL}/details`);
  
  return this.http.get<UserProfile>(`${this.API_URL}/details`).pipe(
    tap(data => console.log('Données reçues:', data)),
    catchError(error => {
      console.error('Erreur API:', error);
      return throwError(() => error);
    })
  );
} 
   

  getUserFollowedShops(): Observable<ShopSummaryDTO[]> {
    return this.http.get<ShopSummaryDTO[]>(`${this.API_URL}/shops`);
  }
  // ==================== PANIER ====================

  getUserCart(): Observable<CartDto> {
    return this.http.get<CartDto>(`${this.API_URL}/cart`);
  }

 

  // ==================== FAVORIS ====================

  getUserFavorites(): Observable<ProductDTO[]> {
    return this.http.get<ProductDTO[]>(`${this.API_URL}/favoris`);
  }

  // addToFavorites(userId: string, productId: string): Observable<ProductFavorite> {
  //   return this.http.post<ProductFavorite>(`${this.apiUrl}/favorites/${userId}/products/${productId}`, {});
  // }

  // removeFromFavorites(userId: string, productId: string): Observable<void> {
  //   return this.http.delete<void>(`${this.apiUrl}/favorites/${userId}/products/${productId}`);
  // }

  // isProductFavorite(userId: string, productId: string): Observable<boolean> {
  //   return this.http.get<boolean>(`${this.apiUrl}/favorites/${userId}/products/${productId}/check`);
  // }

  // getFavoriteCount(userId: string): Observable<number> {
  //   return this.http.get<number>(`${this.apiUrl}/favorites/${userId}/count`);
  // }

  // ==================== COMMANDES ====================

  // getUserOrders(): Observable<UserOrdersDto> { 
  //   return this.http.get<UserOrdersDto>(`${this.API_URL}/orders`);
  // }

  // getOrderById(orderId: string): Observable<Commande> {
  //   return this.http.get<Commande>(`${this.apiUrl}/orders/${orderId}`);
  // }

  // createOrderFromCart(userId: string, shopId: string, deliveryAddress: string, deliveryPostalCode: string): Observable<Commande> {
  //   return this.http.post<Commande>(`${this.apiUrl}/orders/${userId}/create?shopId=${shopId}&deliveryAddress=${deliveryAddress}&deliveryPostalCode=${deliveryPostalCode}`, {});
  // }

  // updateOrderStatus(orderId: string, newEtat: string): Observable<Commande> {
  //   return this.http.put<Commande>(`${this.apiUrl}/orders/${orderId}/status?newEtat=${newEtat}`, {});
  // }

  // cancelOrder(orderId: string): Observable<Commande> {
  //   return this.http.put<Commande>(`${this.apiUrl}/orders/${orderId}/cancel`, {});
  // }

  // ==================== FOLLOWERS BOUTIQUES ====================



  // getUserFollowedShopCount(userId: string): Observable<number> {
  //   return this.http.get<number>(`${this.apiUrl}/shop-followers/user/${userId}/count`);
  // }

  // ==================== STATISTIQUES ====================

  getUserInfo(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.API_URL}/details`);
  }
   getCart(): Observable<Cart> {
    return this.http.get<Cart>(`${this.API_URL}/cart`);
  }
  getOrders():Observable<UserOrdersDto> {
    return this.http.get<UserOrdersDto>(`${this.API_URL}/orders`);
  }
  }
 

 
