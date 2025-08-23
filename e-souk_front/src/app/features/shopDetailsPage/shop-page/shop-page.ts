import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Header } from '../header/header';
import { About } from '../about/about';
import { Products } from '../products/products';
import { Reviews } from '../reviews/reviews';
import { AddReview } from '../add-review/add-review';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-shop-page',
  imports: [CommonModule, Header, About, Products, Reviews, AddReview],
  templateUrl: './shop-page.html',
  styleUrl: './shop-page.css'
})
export class ShopPage {
    name!: string;
  activeTab: "about" | "products" | "reviews" = "about"
  showAddReview = false
  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.name = this.route.snapshot.paramMap.get('name') || '';
    console.log("Shop name récupéré dans ShopPage:", this.name);
  }
  setActiveTab(tab: "about" | "products" | "reviews"): void {
    this.activeTab = tab
  }

  openAddReview(): void {
    this.showAddReview = true
  }

  closeAddReview(): void {
    this.showAddReview = false
  }

  onAddReviewFromReviews(): void {
    this.openAddReview()
  }
}
