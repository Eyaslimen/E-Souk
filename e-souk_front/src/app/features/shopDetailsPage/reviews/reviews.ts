import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ReviewsService } from '../../../services/shopDetails/reviews.service';
import { CommonModule } from '@angular/common';
import { ReviewCard } from '../review-card/review-card';
import { Review } from '../../../interfaces/ProductDetails';
import { ShopDetails } from '../../../services/shop-details';

@Component({
  selector: 'app-reviews',
  imports: [CommonModule, ReviewCard],
  templateUrl: './reviews.html',
  styleUrl: './reviews.css'
})
export class Reviews implements OnInit {
  @Output() addReviewClick = new EventEmitter<void>()
  
  reviews$!: Observable<Review[]>
  averageRating = 0
  ratingDistribution: { [key: number]: number } = {}
  totalReviews = 0
  @Input() name!: string;
  
  constructor(private shopDetails: ShopDetails) {}
  
  ngOnInit(): void {
    this.reviews$ = this.shopDetails.getShopReviews(this.name).pipe(
      map(reviews => {
        this.calculateRatingStats(reviews);
        return reviews;
      })
    );
  }
  
  private calculateRatingStats(reviews: Review[]): void {
    this.totalReviews = reviews.length;
    
    // Reset distribution
    this.ratingDistribution = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
    
    if (reviews.length === 0) {
      this.averageRating = 0;
      return;
    }
    
    // Calculate distribution and average
    let totalRating = 0;
    reviews.forEach(review => {
      this.ratingDistribution[review.rating]++;
      totalRating += review.rating;
    });
    
    this.averageRating = Math.round((totalRating / reviews.length) * 10) / 10;
  }
  
  onAddReview(): void {
    this.addReviewClick.emit()
  }
  
  getStarArray(rating: number): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => (i < Math.floor(rating) ? 1 : 0))
  }
  
  getRatingPercentage(rating: number): number {
    return this.totalReviews > 0 ? (this.ratingDistribution[rating] / this.totalReviews) * 100 : 0
  }
  
  getTotalReviews(): number {
    return this.totalReviews
  }

}