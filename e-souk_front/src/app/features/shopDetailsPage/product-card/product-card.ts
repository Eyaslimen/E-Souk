import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ProductDetails } from '../../../interfaces/ProductDetails';

@Component({
  selector: 'app-product-card',
  imports: [CommonModule],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css'
})
export class ProductCard {
  @Input() product!: ProductDetails;

  generateStars(rating: number): boolean[] {
    return Array(5).fill(false).map((_, i) => i < rating);
  }
}
