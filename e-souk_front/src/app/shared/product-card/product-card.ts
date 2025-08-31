import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, NgClass } from '@angular/common';
import { ProductDetails } from '../../interfaces/ProductDetails';
import { ProductsFavoriteService } from '../../services/products-favorite.service';
import { ToastrNotifications } from '../../services/toastr-notifications';

@Component({
  selector: 'app-product-card',
  imports: [CommonModule],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css'
})
export class ProductCard {
    @Input() product!: ProductDetails;
   isFavorite : boolean = false;
    constructor(private router: Router, private productsFavoriteService: ProductsFavoriteService
      ,     private notification: ToastrNotifications

    ) {}

viewDetails(event: Event): void {
  event.stopPropagation();
  this.router.navigate(['/products', this.product.id]);
}
  
    // addProductToCart(event: Event): void {
    //   event.stopPropagation();
    //   this.addToCart.emit(this.product.id);
    // }
      // Ajouter aux favoris
  ajouterAuxFavoris() {
        this.isFavorite = true;
    const productId = this.product.id;
    this.productsFavoriteService.addProductToFavorite(productId).subscribe(
      result => {
        console.log('Produit ajouté:', result);
        this.notification.success('Produit ajouté au Favoris');
      },
      error => console.error('Erreur:', error)
    );
  }
  
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