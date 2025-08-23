import { Injectable } from "@angular/core"
import { BehaviorSubject, type Observable } from "rxjs"
import { Product } from "../../interfaces/shopDetails"

@Injectable({
  providedIn: "root",
})
export class ProductsService {
  private products: Product[] = [
    {
      id: "1",
      name: "Collier Lune Dorée",
      price: 45,
      originalPrice: 60,
      rating: 4.9,
      reviewsCount: 23,
      category: "Colliers",
      isPopular: true,
      image: "products/bag1.jpg",
      createdAt: new Date("2024-01-15"),
    },
    {
      id: "2",
      name: "Boucles d'oreilles Étoiles",
      price: 32,
      rating: 4.7,
      reviewsCount: 18,
      category: "Boucles d'oreilles",
      image: "products/bag2.jpg",
      isPopular: true,
      soldCount: 38,
      isNew: true,
      createdAt: new Date("2024-01-10"),
    },
    {
      id: "3",
      name: "Bracelet Perles Naturelles",
      price: 28,
      rating: 4.6,
      reviewsCount: 15,
      category: "Bracelets",
      image: "products/bag1.jpg",
      isNew: true,
      isPopular: true,
      createdAt: new Date("2024-01-12"),
    },
    {
      id: "4",
      name: "Bague Vintage Or Rose",
      price: 85,
      rating: 4.8,
      reviewsCount: 45,
      category: "Bagues",
      isNew: true,
      image: "products/bag3.jpg",
      isPopular: true,
      soldCount: 45,
      createdAt: new Date("2024-01-05"),
    },
    {
      id: "5",
      name: "Collier Perles Classique",
      price: 55,
      rating: 4.5,
      reviewsCount: 38,
      category: "Colliers",
      image: "products/bag4.jpg",
      soldCount: 38,
      isNew: true,
            isPopular: true,
      createdAt: new Date("2024-01-08"),
    },
    {
      id: "6",
      name: "Boucles d'oreilles Pendantes",
      price: 42,
      rating: 4.7,
      reviewsCount: 29,
      category: "Boucles d'oreilles",
      image: "products/bag1.jpg",
      soldCount: 29,
            isPopular: true,

      createdAt: new Date("2024-01-03"),
    },
  ]

  private productsSubject = new BehaviorSubject<Product[]>(this.products)

  getAllProducts(): Observable<Product[]> {
    return this.productsSubject.asObservable()
  }

  getNewProducts(): Observable<Product[]> {
    const newProducts = this.products.filter((p) => p.isNew).slice(0, 4)
    return new BehaviorSubject(newProducts).asObservable()
  }

  getPopularProducts(): Observable<Product[]> {
    const popularProducts = this.products.filter((p) => p.isPopular || (p.soldCount && p.soldCount > 30))
    return new BehaviorSubject(popularProducts).asObservable()
  }

  getCategories(): string[] {
    return [...new Set(this.products.map((p) => p.category))]
  }
}
