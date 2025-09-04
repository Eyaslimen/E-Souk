import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ShopDetails } from '../../../services/shop-details';
import { ReviewRequest } from '../../../interfaces/ProductDetails';
import { ToastrNotifications } from '../../../services/toastr-notifications';

@Component({
  selector: 'app-add-review',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-review.html',
  styleUrl: './add-review.css'
})
export class AddReview{
  @Output() close = new EventEmitter<void>()
  @Input() name!: string;

  reviewForm: FormGroup
  isSubmitting = false

  constructor(
    private fb: FormBuilder,
    private shopDetails: ShopDetails,
    private notification : ToastrNotifications
  ) {
    this.reviewForm = this.fb.group({
      rating: [""],
      content: [""],
    })
  }


  getStarArray(): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => i + 1)
  }

  onSubmit(): void {
      this.isSubmitting = true

      const reviewData: ReviewRequest = {
        content: this.reviewForm.value.content,
        rating: this.reviewForm.value.rating,
        reviewType: 'SHOP'
      }
      console.log("Review Data:", reviewData);
      // Simulate API call delay
setTimeout(() => {
  this.shopDetails.addReview(reviewData, this.name).subscribe({
    next: (response) => {
      console.log('Review created successfully:', response);
      this.isSubmitting = false;
      this.onClose();
      this.notification.success("Avis ajouté avec succées");

    },
    error: (error) => {
      console.error('Error creating review:', error);
      this.isSubmitting = false;
      // Optionnel: afficher un message d'erreur à l'utilisateur
      // this.showErrorMessage('Failed to create review');
    }
  });
}, 1000);
    
  }

  onClose(): void {
    this.close.emit()
  }

  onBackdropClick(event: Event): void {
    if (event.target === event.currentTarget) {
      this.onClose()
    }
  }
}
