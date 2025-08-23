import { Component, type OnInit } from "@angular/core"
import { ProductDetails } from "../../../interfaces/ProductDetails";
import { ProductCard } from '../../../shared/product-card/product-card';
import { CommonModule } from "@angular/common";
import { FormsModule, NgModel } from "@angular/forms";


@Component({
  standalone: true,
  selector: 'app-products',
  imports: [ProductCard,CommonModule,FormsModule],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products implements OnInit {
  searchTerm = ""
  selectedFilter = "Toutes"
  viewMode: "grid" | "list" = "grid"

  filters = ["Toutes", "Mis en avant", "Électronique", "Audio", "Wearables", "Informatique"]
  // id: string; // UUID as string
  //   name: string;
  //   description: string;
  //   price: number;
  //   picture: string;
  //   categoryName: string;
  //   shopName: string;
  recommendedProducts: ProductDetails[] = [
    {
      id: "1",
      name: "Smartphone Pro Max",
      categoryName: "Électronique",
      price: 899,
      picture: "/modern-smartphone.png",
      description: "Un smartphone haut de gamme avec un écran AMOLED et un processeur puissant.",
      shopName: "Tech Store",
    },
    {
      id: "2",
      name: "Casque Audio Premium",
      categoryName: "Audio",
      price: 299,
      picture: "/diverse-people-listening-headphones.png",
      description: "Un casque audio premium avec une qualité sonore exceptionnelle.",
      shopName: "Audio Hub",
    },
    {
      id: "3",
      name: "Montre Connectée Sport",
      categoryName: "Wearables",
      price: 399,
      picture: "/modern-smartwatch.png",
      description: "Une montre connectée sportive avec suivi de la condition physique.",
      shopName: "Wearable Store", },
  ]

  allProducts: ProductDetails[] = [
    ...this.recommendedProducts,
    {
      id: "1",
      name: "Smartphone Pro Max",
      categoryName: "Électronique",
      price: 899,
      picture: "/modern-smartphone.png",
      description: "Un smartphone haut de gamme avec un écran AMOLED et un processeur puissant.",
      shopName: "Tech Store",
    },
    {
      id: "2",
      name: "Casque Audio Premium",
      categoryName: "Audio",
      price: 299,
      picture: "/diverse-people-listening-headphones.png",
      description: "Un casque audio premium avec une qualité sonore exceptionnelle.",
      shopName: "Audio Hub",
    },
    {
      id: "3",
      name: "Montre Connectée Sport",
      categoryName: "Wearables",
      price: 399,
      picture: "/modern-smartwatch.png",
      description: "Une montre connectée sportive avec suivi de la condition physique.",
      shopName: "Wearable Store", },
  ]

  filteredProducts: ProductDetails[] = []

  constructor() {}

  ngOnInit(): void {
    // this.filterProducts()
  }

  onSearchChange(): void {
    // this.filterProducts()
  }

  onFilterChange(filter: string): void {
    this.selectedFilter = filter
    // this.filterProducts()
  }

  toggleViewMode(): void {
    this.viewMode = this.viewMode === "grid" ? "list" : "grid"
  }

  // filterProducts(): void {
  //   let products = [...this.allProducts]

  //   // Filter by search term
  //   if (this.searchTerm.trim()) {
  //     products = products.filter(
  //       (product) =>
  //         product.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
  //         product.category.toLowerCase().includes(this.searchTerm.toLowerCase()),
  //     )
  //   }

  //   // Filter by category
  //   if (this.selectedFilter !== "Toutes") {
  //     if (this.selectedFilter === "Mis en avant") {
  //       products = products.filter((product) => product.isFeatured)
  //     } else {
  //       products = products.filter((product) => product.category === this.selectedFilter)
  //     }
  //   }

  //   this.filteredProducts = products
  // }

  // onAddToCart(product: Product): void {
  //   console.log("Ajouté au panier:", product)
  //   // Implement add to cart logic
  // }

  // onToggleFavorite(product: Product): void {
  //   console.log("Favori togglé:", product)
  //   // Implement favorite toggle logic
  // }
}
