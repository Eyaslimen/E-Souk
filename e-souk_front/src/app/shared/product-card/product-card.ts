import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { ProductDetails } from '../../interfaces/ProductDetails';

@Component({
  selector: 'app-product-card',
  imports: [NgIf],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css'
})
export class ProductCard {
    @Input() product!: ProductDetails;
  
    constructor(private router: Router) {}
  
    // toggleFavorite(event: Event): void {
    //   event.stopPropagation();
    //   this.favoriteToggle.emit(this.product.id);
    // }
  
    // addProductToCart(event: Event): void {
    //   event.stopPropagation();
    //   this.addToCart.emit(this.product.id);
    // }
  
    // viewDetails(): void {
    //   this.router.navigate(['/produit', this.product.id]);
    // }
  
    // viewShop(event: Event): void {
    //   event.stopPropagation();
    //   this.router.navigate(['/boutique', this.product.shopId]);
    // }
  
    formatPrice(price: number): string {
      return new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR'
      }).format(price);
    }
  
    // getDiscountedPrice(): number {
    //   if (this.product.isOnSale && this.product.discountPercentage) {
    //     return this.product.price * (1 - this.product.discountPercentage / 100);
    //   }
    //   return this.product.price;
    // }
  
    // isOutOfStock(): boolean {
    //   return this.product.stock === 0;
    // }
  }