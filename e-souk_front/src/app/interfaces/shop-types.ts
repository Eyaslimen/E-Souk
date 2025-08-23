// src/app/types/shop.types.ts
// export interface CreateShopRequestDTO {
//   brandName: string;
//   description?: string;
//   deliveryFee: number;
//   address: string;
//   logoPicture?: File;
// }
export interface CreateShopRequestDTO {
  brandName: string;
  bio:string,
  description?: string;
  categoryName:string,
  deliveryFee: number;
  address: string;
  phone:string,
  facebookLink:string,
  instagramLink:string;
  logoPicture?: File;
}
export interface ShopConfirmationData extends CreateShopRequestDTO {
  logoPictureUrl?: string; // Pour l'aperçu
}

// src/app/types/product.types.ts
export interface CreateProductRequestDTO {
  shopId: string;
  name: string;
  category: string; // Sera transformé en categoryName côté service
  description?: string;
  attributes: ProductAttribute[];
  variants: ProductVariant[];
  images?: File[]; // ✅ AJOUT: Support des images
}

export interface ProductAttribute {
  name: string;
  values: string[];
}

// ✅ CORRECTION: Structure des variantes pour le frontend
export interface ProductVariant {
  attributes: { [key: string]: string }; // Ex: { "taille": "S", "couleur": "Rouge" }
  price: number;
  stock: number;
}

export interface ProductSummary {
  id: string;
  name: string;
  category: string;
  variantCount: number;
  totalStock: number;
  minPrice: number;
  maxPrice: number;
}
export interface BackendProductVariant {
  attributeValues: Array<{
    attributeName: string;
    value: string;
  }>;
  price: number;
  stock: number;
}

// src/app/types/onboarding.types.ts
export enum OnboardingStep {
  CREATE_SHOP = 1,
  ADD_PRODUCTS = 2,
  COMPLETED = 3
}

export interface OnboardingState {
  currentStep: OnboardingStep;
  shopData?: CreateShopRequestDTO;
  shopId?: string;
  products: ProductSummary[];
} 

// ✅ AJOUT: Interface pour la réponse du backend lors de la création d'un produit
export interface ProductResponseDTO {
  id: string;
  name: string;
  description?: string;
  category: string;
  shopId: string;
  createdAt: string;
  updatedAt: string;
  variants?: any[]; // Structure complète des variantes retournées par le backend
}

// ✅ AJOUT: Interface pour la structure complète d'un produit
export interface ProductDetailsDTO {
  id: string;
  name: string;
  description?: string;
  category: string;
  shopId: string;
  images?: string[];
  attributes: ProductAttribute[];
  variants: Array<{
    id: string;
    attributeValues: Array<{
      attributeName: string;
      value: string;
    }>;
    price: number;
    stock: number;
  }>;
  createdAt: string;
  updatedAt: string;
}