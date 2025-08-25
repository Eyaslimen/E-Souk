import { Component, type OnInit, type OnDestroy, type ElementRef, ViewChild } from "@angular/core"
import { ProductDetails } from "../../../interfaces/ProductDetails"
import { ProductCard } from "../../../shared/product-card/product-card"
import { CommonModule } from "@angular/common"

// interface ProductDetails {
//   id: string
//   name: string
//   categoryName: string
//   price: number
//   picture: string
//   description: string
//   shopName: string
// }
@Component({
  selector: 'app-product-carousel',
  imports: [ProductCard,CommonModule],
  templateUrl: "./product-carousel.html",
  styleUrl: './product-carousel.css'
})

export class ProductCarousel implements OnInit, OnDestroy {
  @ViewChild("carouselContainer", { static: true }) carouselContainer!: ElementRef

  // Vos produits recommandés
  recommendedProducts: ProductDetails[] = [
    {
      id: "1",
      name: "Smartphone Pro Max",
      categoryName: "technologie",
      price: 899,
      picture: "bag.jpg",
      description: "Un smartphone haut de gamme avec un écran AMOLED et un processeur puissant.",
      shopName: "Tech Store",
    },
    {
      id: "2",
      name: "Casque Audio Premium",
      categoryName: "technologie",
      price: 299,
      picture: "/diverse-people-listening-headphones.png",
      description: "Un casque audio premium avec une qualité sonore exceptionnelle.",
      shopName: "Audio Hub",
    },
    {
      id: "4",
      name: "Pc portable",
      categoryName: "sport",
      price: 399,
      picture: "/modern-smartwatch.png",
      description: "Une montre connectée sportive avec suivi de la condition physique.",
      shopName: "Wearable Store",
    },
    {
      id: "5",
      name: "Pc portable",
      categoryName: "sport",
      price: 399,
      picture: "/modern-smartwatch.png",
      description: "Une montre connectée sportive avec suivi de la condition physique.",
      shopName: "Wearable Store",
    },
  ]

  // Produits dupliqués pour l'effet de boucle infinie
  duplicatedProducts: ProductDetails[] = []

  private animationId = 0
  private scrollPosition = 0
  public isHovered = false
  private readonly scrollSpeed: number = 0.5 // Vitesse de défilement (pixels par frame)

  ngOnInit() {
    // Dupliquer les produits pour créer une boucle infinie
    this.duplicatedProducts = [...this.recommendedProducts, ...this.recommendedProducts, ...this.recommendedProducts]

    // Démarrer l'animation après que la vue soit initialisée
    setTimeout(() => {
      this.startAnimation()
    }, 100)
  }

  ngOnDestroy() {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId)
    }
  }

  private startAnimation() {
    const animate = () => {
      if (!this.isHovered) {
        this.scrollPosition += this.scrollSpeed

        // Calculer la largeur d'un produit (320px + 16px de gap)
        const productWidth = 336
        const totalWidth = this.recommendedProducts.length * productWidth

        // Réinitialiser la position quand on a défilé d'une longueur complète
        if (this.scrollPosition >= totalWidth) {
          this.scrollPosition = 0
        }

        // Appliquer la transformation
        if (this.carouselContainer?.nativeElement) {
          this.carouselContainer.nativeElement.style.transform = `translateX(-${this.scrollPosition}px)`
        }
      }

      this.animationId = requestAnimationFrame(animate)
    }

    animate()
  }

  onMouseEnter() {
    this.isHovered = true
  }

  onMouseLeave() {
    this.isHovered = false
  }

  trackByProductId(index: number, product: ProductDetails): string {
    return `${product.id}-${index}`
  }
}