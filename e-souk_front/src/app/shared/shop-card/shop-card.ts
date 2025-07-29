import { Component, Input } from '@angular/core';
import { Shop } from '../../interfaces/Shop';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
@Component({
  selector: 'app-shop-card',
  imports: [NgIf],
  templateUrl: './shop-card.html',
  styleUrl: './shop-card.css'
})
export class ShopCard {
    @Input() shop!: Shop;
  
    constructor(private router: Router) {}
  
    visitShop(): void {
      this.router.navigate(['/boutique', this.shop.id]);
    }
  
    getInitials(name: string): string {
      return name.charAt(0).toUpperCase();
    }
  
    formatFollowers(count: number): string {
      if (count >= 1000) {
        return (count / 1000).toFixed(1) + 'k';
      }
      return count.toString();
    }
  }