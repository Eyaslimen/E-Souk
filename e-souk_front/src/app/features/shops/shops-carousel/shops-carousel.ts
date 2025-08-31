import { Component, type OnInit, type OnDestroy, type ElementRef, ViewChild } from "@angular/core"
import { Shop } from "../../../interfaces/Shop"
import { ShopCard } from "../../../shared/shop-card/shop-card"
import { CommonModule } from "@angular/common"

@Component({
  selector: 'app-shops-carousel',
  imports: [ShopCard, CommonModule],
  templateUrl: "./shops-carousel.html",
  styleUrl: './shops-carousel.css'
})

export class ShopsCarousel implements OnInit, OnDestroy {
  @ViewChild("carouselContainer", { static: true }) carouselContainer!: ElementRef

  // Boutiques recommandées
  recommendedShops: Shop[] = [
    {
      id: "1",
      brandName: "Tech Store",
      logoPicture: "/shopLogo.png",
      description: "Boutique spécialisée dans les produits technologiques de dernière génération.",
      ownerUsername: "tech_owner",
      address: "Tunis",
      productCount: 45,
      followerCount: 1250,
      categoryName: "Technologie"
    },
    {
      id: "2", 
      brandName: "Fashion Hub",
      logoPicture: "/shopLogo.png",
      description: "Mode tendance et accessoires pour tous les styles.",
      ownerUsername: "fashion_owner",
      address: "Sfax",
      productCount: 78,
      followerCount: 890,
      categoryName: "Mode"
    },
    {
      id: "3",
      brandName: "Home & Garden",
      logoPicture: "/shopLogo.png", 
      description: "Décoration et articles pour la maison et le jardin.",
      ownerUsername: "home_owner",
      address: "Sousse",
      productCount: 32,
      followerCount: 567,
      categoryName: "Maison"
    },
    {
      id: "4",
      brandName: "Sport Elite",
      logoPicture: "/shopLogo.png",
      description: "Équipements et vêtements de sport de qualité professionnelle.",
      ownerUsername: "sport_owner", 
      address: "Monastir",
      productCount: 56,
      followerCount: 723,
      categoryName: "Sport"
    },
    {
      id: "5",
      brandName: "Beauty Corner",
      logoPicture: "/shopLogo.png",
      description: "Cosmétiques et produits de beauté naturels.",
      ownerUsername: "beauty_owner",
      address: "Hammamet", 
      productCount: 41,
      followerCount: 445,
      categoryName: "Beauté"
    }
  ]

  // Boutiques dupliquées pour l'effet de boucle infinie
  duplicatedShops: Shop[] = []

  private animationId = 0
  private scrollPosition = 0
  public isHovered = false
  private readonly scrollSpeed: number = 0.3 // Vitesse de défilement (pixels par frame)

  ngOnInit() {
    // Dupliquer les boutiques pour créer une boucle infinie
    this.duplicatedShops = [...this.recommendedShops, ...this.recommendedShops, ...this.recommendedShops]

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

        // Calculer la largeur d'une boutique (350px + 20px de gap)
        const shopWidth = 370
        const totalWidth = this.recommendedShops.length * shopWidth

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

  trackByShopId(index: number, shop: Shop): string {
    return `${shop.id}-${index}`
  }
}
