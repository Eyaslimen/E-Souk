import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CartDto, ProductDTO, ShopSummaryDTO, UserProfile } from '../interfaces/user-info';


@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly API_URL = 'http://localhost:8080/api/cart';
  constructor(private http: HttpClient) { }
    // private UUID productId;
    // private Map<String, String> selectedAttributes;
    // private Integer quantity;

 addItemToCart(productId: string, selectedAttributes: { [key: string]: string }, quantity: number): Observable<String> {
    return this.http.post<String>(`${this.API_URL}/add`, {
      productId,
      selectedAttributes,
      quantity
    });
  }

  updateCartItemQuantity(userId: string, variantId: string, quantity: number): Observable<CartDto> {
    return this.http.put<CartDto>(`${this.API_URL}/cart/${userId}/items/${variantId}?quantity=${quantity}`, {});
  }

  removeItemFromCart(userId: string, variantId: string): Observable<CartDto> {
    return this.http.delete<CartDto>(`${this.API_URL}/cart/${userId}/items/${variantId}`);
  }

  clearCart(userId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/cart/${userId}`);
  }
}