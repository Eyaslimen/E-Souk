import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CartDto, ProductDTO, ShopSummaryDTO, UserProfile } from '../interfaces/user-info';


@Injectable({
  providedIn: 'root'
})
export class CommandeService {
  private readonly API_URL = 'http://localhost:8080/api/orders';
  constructor(private http: HttpClient) { }
    // private UUID productId;
    // private Map<String, String> selectedAttributes;
    // private Integer quantity;

addOrder(shopId: string): Observable<string> {
  return this.http.post<string>(`${this.API_URL}/add/${shopId}`, {});
}

}