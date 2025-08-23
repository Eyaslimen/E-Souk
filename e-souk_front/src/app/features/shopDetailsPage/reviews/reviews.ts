import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
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
  @Input() name!: string;

  constructor(private shopDetails: ShopDetails) {}

  ngOnInit(): void {
    this.reviews$ = this.shopDetails.getShopReviews(this.name);
  }

  onAddReview(): void {
    this.addReviewClick.emit()
  }

  getStarArray(rating: number): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => (i < rating ? 1 : 0))
  }

  getRatingPercentage(rating: number): number {
    const total = Object.values(this.ratingDistribution).reduce((sum, count) => sum + count, 0)
    return total > 0 ? (this.ratingDistribution[rating] / total) * 100 : 0
  }

  getTotalReviews(): number {
    return Object.values(this.ratingDistribution).reduce((sum, count) => sum + count, 0)
  }
}
