import { Component, ChangeDetectionStrategy, signal, ViewEncapsulation } from '@angular/core';
import { Shop } from '../interfaces/Shop';
import { ShopCard } from "../shared/shop-card/shop-card";
import { NgFor } from '@angular/common';
import { ProductCard } from "../shared/product-card/product-card";
import { ProductDetails } from '../interfaces/ProductDetails';

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
  imports: [ShopCard, NgFor, ProductCard],
})
export class LandingPageComponent {
  isMobileMenuOpen = signal(false);
  shops: Shop[] = [
    {
      id: '1',
      brandName: 'Artisan Café',
      logoPicture: 'shopLogo.png',
      description: 'Torréfacteur artisanal proposant des cafés de spécialité issus du commerce équitable. Découvrez nos mélanges uniques et nos grains d\'exception.',
      ownerUsername: 'Marie Dubois',
      address: 'Paris, France',
      productCount: 127,
      followerCount: 1250,
      categoryName: 'Alimentation',
    },
    {
      id: '2',
      brandName: 'Créations Luna',
      logoPicture: 'shopLogo.png',
      description: 'Bijoux artisanaux faits main avec des matériaux nobles. Chaque pièce est unique et créée avec passion dans notre atelier parisien.',
      ownerUsername: 'Sophie Martin',
      address: 'Lyon, France',
      productCount: 89,
      followerCount: 890,
      categoryName: 'Bijoux',
    },
    {
      id: '3',
      brandName: 'Eya Shop',
      logoPicture: 'shopLogo.png',
      description: 'Vêtements chic de tendance Vêtements chic de tendance Vêtements chic de tendance',
      ownerUsername: 'Eya Slimen',
      address: 'Sfax, Tunis',
      productCount: 89,
      followerCount: 890,
      categoryName: 'Bijoux',
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
    // id: string; // UUID as string
    // name: string;
    // description: string;
    // price: number;
    // picture: string;
    // categoryName: string;
    // shopName: string;
  public products: ProductDetails[] = [
    {
      id: '1',
      name: 'Café Arabica Premium',
      price: 24.99,
      picture: 'products/arabica.png',
      description: 'Café arabica de haute qualité, torréfié artisanalement dans notre atelier. Notes fruitées et chocolatées.',
      categoryName: 'Alimentation',
      shopName: 'Artisan Café',
    },
    {
      id: '2',
      name: 'Collier Luna Argenté',
      price: 89.99,
      picture: 'products/collierarg.png',
      description: 'Collier artisanal en argent 925 avec pendentif lune. Pièce unique créée à la main.',
      categoryName: 'Bijoux',
      shopName: 'Créations Luna',
    },
    {
      id: '3',
      name: 'Savon Naturel Lavande',
      price: 8.50,
      picture: 'products/savon.png',
      description: 'Savon artisanal à la lavande bio. Hydratant et apaisant pour tous types de peau.',
      categoryName: 'Cosmétiques',
      shopName: 'Éco Maison',
    },
    {
      id: '4',
      name: 'Robe Vintage Années 80',
      price: 45.00,
      picture: 'products/robe80.png',
      description: 'Robe vintage authentique des années 80. Taille M, excellent état.',
      categoryName: 'Mode',
      shopName: 'Mode Vintage',
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