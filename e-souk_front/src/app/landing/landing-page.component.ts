import { Component, ChangeDetectionStrategy, signal, ViewEncapsulation } from '@angular/core';
import { NavbarComponent } from '../layouts/navbar/navbar.component';
import { Shop } from '../interfaces/Shop';
import { ShopCard } from "../shared/shop-card/shop-card";
import { NgFor } from '@angular/common';
import { Product } from '../interfaces/Product';
import { ProductCard } from "../shared/product-card/product-card";
import { Footer } from "../layouts/footer/footer";

interface Testimonial {
  text: string;
  name: string;
  role: string;
  initials: string;
  avatarClass: string;
}

interface FAQ {
  question: string;
  answer: string;
}

@Component({
  selector: 'landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  imports: [NavbarComponent, ShopCard, NgFor, ProductCard, Footer],
})
export class LandingPageComponent {
  isMobileMenuOpen = signal(false);
  shops: Shop[] = [
    {
      id: '1',
      brandName: 'Artisan Café',
      logo: 'shopLogo.png',
      description: 'Torréfacteur artisanal proposant des cafés de spécialité issus du commerce équitable. Découvrez nos mélanges uniques et nos grains d\'exception.',
      owner: 'Marie Dubois',
      location: 'Paris, France',
      rating: 4.8,
      totalReviews: 127,
      followers: 1250,
      category: 'Alimentation',
      isVerified: true
    },
    {
      id: '2',
      brandName: 'Créations Luna',
      logo: 'shopLogo.png',
      description: 'Bijoux artisanaux faits main avec des matériaux nobles. Chaque pièce est unique et créée avec passion dans notre atelier parisien.',
      owner: 'Sophie Martin',
      location: 'Lyon, France',
      rating: 4.9,
      totalReviews: 89,
      followers: 890,
      category: 'Bijoux',
      isVerified: false
    },
    {
      id: '3',
      brandName: 'Eya Shop',
      logo: 'shopLogo.png',
      description: 'Vêtements chic de tendance Vêtements chic de tendance Vêtements chic de tendance',
      owner: 'Eya Slimen',
      location: 'Sfax, Tunis',
      rating: 4.9,
      totalReviews: 89,
      followers: 890,
      category: 'Bijoux',
      isVerified: true
    },
    // Ajoutez d'autres boutiques...
  ];
  // featuredShops = signal<Shop[]>([
  //   {
  //     id: 1,
  //     name: "Artisan Boulangerie",
  //     description: "Pains et viennoiseries artisanales",
  //     image: "https://via.placeholder.com/300x200?text=Boulangerie",
  //     rating: 4.8,
  //     followers: 1250,
  //     category: "Alimentation",
  //   },
  //   {
  //     id: 2,
  //     name: "Mode Éthique",
  //     description: "Vêtements éco-responsables",
  //     image: "https://via.placeholder.com/300x200?text=Mode",
  //     rating: 4.6,
  //     followers: 890,
  //     category: "Mode",
  //   },
  //   {
  //     id: 3,
  //     name: "Tech Local",
  //     description: "Réparation et accessoires tech",
  //     image: "https://via.placeholder.com/300x200?text=Tech",
  //     rating: 4.9,
  //     followers: 650,
  //     category: "Technologie",
  //   },
  // ]);
  public products: Product[] = [
    {
      id: 1,
      name: 'Café Arabica Premium',
      price: 24.99,
      originalPrice: 29.99,
      image: 'products/arabica.png',
      images: ['assets/images/cafe-arabica.jpg', 'assets/images/cafe-arabica-2.jpg'],
      description: 'Café arabica de haute qualité, torréfié artisanalement dans notre atelier. Notes fruitées et chocolatées.',
      category: 'Alimentation',
      stock: 15,
      shopId: '1',
      shopName: 'Artisan Café',
      shopLogo: 'shopLogo.png',
      rating: 4.8,
      totalReviews: 45,
      isOnSale: true,
      discountPercentage: 17,
      isFavorite: false,
      isInCart: false
    },
    {
      id: 2,
      name: 'Collier Luna Argenté',
      price: 89.99,
      image: 'products/collierarg.png',
      images: ['assets/images/collier-luna.jpg'],
      description: 'Collier artisanal en argent 925 avec pendentif lune. Pièce unique créée à la main.',
      category: 'Bijoux',
      stock: 3,
      shopId: '2',
      shopName: 'Créations Luna',
      shopLogo: 'shopLogo.png',
      rating: 4.9,
      totalReviews: 23,
      isOnSale: false,
      isFavorite: true,
      isInCart: false
    },
    {
      id: 3,
      name: 'Savon Naturel Lavande',
      price: 8.50,
      image: 'products/savon.png',
      description: 'Savon artisanal à la lavande bio. Hydratant et apaisant pour tous types de peau.',
      category: 'Cosmétiques',
      stock: 25,
      shopId: '3',
      shopName: 'Éco Maison',
      shopLogo: 'shopLogo.png',
      rating: 4.6,
      totalReviews: 67,
      isOnSale: false,
      isFavorite: false,
      isInCart: false
    },
    {
      id: 4,
      name: 'Robe Vintage Années 80',
      price: 45.00,
      originalPrice: 60.00,
      image: 'products/robe80.png',
      description: 'Robe vintage authentique des années 80. Taille M, excellent état.',
      category: 'Mode',
      stock: 0,
      shopId: '4',
      shopName: 'Mode Vintage',
      shopLogo: 'shopLogo.png',
      rating: 4.7,
      totalReviews: 12,
      isOnSale: true,
      discountPercentage: 25,
      isFavorite: false,
      isInCart: false
    }]
  // featuredProducts = signal<Product[]>([
  //   {
  //     id: 1,
  //     name: "Croissant Artisanal",
  //     price: 1.5,
  //     image: "https://via.placeholder.com/200x200?text=Croissant",
  //     shop: "Artisan Boulangerie",
  //     rating: 4.9,
  //   },
  //   {
  //     id: 2,
  //     name: "T-shirt Bio",
  //     price: 29.99,
  //     image: "https://via.placeholder.com/200x200?text=T-shirt",
  //     shop: "Mode Éthique",
  //     rating: 4.7,
  //   },
  //   {
  //     id: 3,
  //     name: "Coque iPhone",
  //     price: 15.99,
  //     image: "https://via.placeholder.com/200x200?text=Coque",
  //     shop: "Tech Local",
  //     rating: 4.8,
  //   },
  //   {
  //     id: 4,
  //     name: "Pain Complet",
  //     price: 3.2,
  //     image: "https://via.placeholder.com/200x200?text=Pain",
  //     shop: "Artisan Boulangerie",
  //     rating: 4.6,
  //   },
  // ]);

  testimonials = signal<Testimonial[]>([
    {
      text: "J'adore pouvoir découvrir des produits locaux et soutenir les petites entreprises de ma région. La plateforme est très facile à utiliser !",
      name: "Marie Dubois",
      role: "Cliente depuis 6 mois",
      initials: "MD",
      avatarClass: "bg-blue-100"
    },
    {
      text: "Grâce à E-Souk, j'ai pu développer ma boulangerie en ligne sans compétences techniques. Mes ventes ont augmenté de 40% !",
      name: "Pierre Martin",
      role: "Artisan Boulanger",
      initials: "PM",
      avatarClass: "bg-green-100"
    },
    {
      text: "La livraison est rapide et les produits sont toujours de qualité. J'ai trouvé des créateurs incroyables que je n'aurais jamais découverts autrement.",
      name: "Sophie Laurent",
      role: "Cliente fidèle",
      initials: "SL",
      avatarClass: "bg-purple-100"
    }
  ]);

  faqs = signal<FAQ[]>([
    {
      question: "Comment créer ma boutique en ligne ?",
      answer: "Il vous suffit de vous inscrire en tant que vendeur, de configurer votre boutique avec vos informations, puis d'ajouter vos produits. Notre assistant IA peut vous aider."
    },
    {
      question: "Quels sont les frais pour les vendeurs ?",
      answer: "L'inscription et la création de votre boutique sont entièrement gratuites. Nous prélevons uniquement une petite commission sur chaque vente réalisée."
    },
    {
      question: "Comment fonctionne la livraison ?",
      answer: "Chaque vendeur définit ses propres conditions de livraison. Les informations sont clairement affichées sur chaque boutique avant l'achat."
    },
    {
      question: "Puis-je suivre mes commandes ?",
      answer: "Oui, vous pouvez suivre toutes vos commandes depuis votre espace client. Vous recevrez des notifications à chaque étape du processus."
    }
  ]);

  toggleMobileMenu() {
    this.isMobileMenuOpen.update(open => !open);
  }
} 