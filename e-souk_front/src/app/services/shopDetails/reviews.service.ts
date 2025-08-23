import { Injectable } from "@angular/core"
import { BehaviorSubject, type Observable } from "rxjs"
import { Review } from "../../interfaces/shopDetails"

@Injectable({
  providedIn: "root",
})
export class ReviewsService {
  private reviews: Review[] = [
    {
      id: "1",
      customerName: "Marie Laurent",
      rating: 5,
      comment:
        "Absolument magnifique ! La qualité est exceptionnelle et le service client parfait.",
      date: new Date("2024-01-15"),
      isVerified: true,
    },
    {
      id: "2",
      customerName: "Alex Dubois",
      rating: 5,
      comment:
        "Sophie est professionnelle repond rapidement aux questions.",
      date: new Date("2024-01-12"),
      isVerified: true,
    },

    {
      id: "3",
      customerName: "Emma R.",
      rating: 4,
      comment: "Très satisfaite de mon achat. Les bijoux sont encore plus beaux en vrai.",
      date: new Date("2024-01-10"),
      isVerified: false,
    },
  ]

  private reviewsSubject = new BehaviorSubject<Review[]>(this.reviews)

  getAllReviews(): Observable<Review[]> {
    return this.reviewsSubject.asObservable()
  }

  getPositiveReviews(): Observable<Review[]> {
    const positiveReviews = this.reviews.filter((r) => r.rating >= 4)
    return new BehaviorSubject(positiveReviews).asObservable()
  }

  addReview(review: Omit<Review, "id" | "date">): void {
    const newReview: Review = {
      ...review,
      id: (this.reviews.length + 1).toString(),
      date: new Date(),
    }
    this.reviews.unshift(newReview)
    this.reviewsSubject.next([...this.reviews])
  }

  getAverageRating(): number {
    if (this.reviews.length === 0) return 0
    const sum = this.reviews.reduce((acc, review) => acc + review.rating, 0)
    return Math.round((sum / this.reviews.length) * 10) / 10
  }

  getRatingDistribution(): { [key: number]: number } {
    const distribution = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 }
    this.reviews.forEach((review) => {
      distribution[review.rating as keyof typeof distribution]++
    })
    return distribution
  }
}
