import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ContactInfo, Product, Review, ShopStats } from '../../../interfaces/shopDetails';
import { ShopService } from '../../../services/shopDetails/shop.service';
import { ProductsService } from '../../../services/shopDetails/products.service';
import { ReviewsService } from '../../../services/shopDetails/reviews.service';
import { CommonModule } from '@angular/common';
import { ProductCard } from '../product-card/product-card';
import { ReviewCard } from '../review-card/review-card';
import { ShopGeneralDetails } from '../../../interfaces/ProductDetails';
import { ShopDetails } from '../../../services/shop-details';

@Component({
  selector: "app-about",
  imports: [CommonModule, ProductCard, ReviewCard],
  templateUrl: "./about.html",
  styleUrl: "./about.css",
})
export class About implements OnInit {
  stats$!: Observable<ShopStats>
  contact$!: Observable<ContactInfo>
  newProducts$!: Observable<Product[]>
  popularProducts$!: Observable<Product[]>
  positiveReviews$!: Observable<Review[]>
  currentSlide = 0
  itemsPerSlide = 3
  generalDetails$!: Observable<ShopGeneralDetails>;
  @Input() name!: string;

  constructor(
    private shopService: ShopService,
    private productsService: ProductsService,
    private reviewsService: ReviewsService,
    private shopDetailsService: ShopDetails
  ) {}

  ngOnInit(): void {
    this.stats$ = this.shopService.getStats()
    this.contact$ = this.shopService.getContact()
    this.newProducts$ = this.productsService.getNewProducts()
    this.popularProducts$ = this.productsService.getPopularProducts()
    this.positiveReviews$ = this.reviewsService.getPositiveReviews()
    this.generalDetails$ = this.shopDetailsService.getShopDetails(this.name);
    console.log(this.generalDetails$);

  }

  getStarArray(rating: number): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => (i < Math.floor(rating) ? 1 : 0))
  }

  getVisibleProducts(products: Product[]): Product[] {
    if (!products) return []
    const start = this.currentSlide * this.itemsPerSlide
    return products.slice(start, start + this.itemsPerSlide)
  }

  nextSlide(): void {
    // Implementation for next slide navigation
    this.currentSlide++
  }

  previousSlide(): void {
    // Implementation for previous slide navigation
    if (this.currentSlide > 0) {
      this.currentSlide--
    }
  }
}
