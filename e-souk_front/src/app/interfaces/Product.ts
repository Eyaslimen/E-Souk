export interface Product {
    id: number;
    name: string;
    price: number;
    originalPrice?: number; // Prix barré si en promotion
    image: string;
    images?: string[]; // Images supplémentaires
    description: string;
    category: string;
    stock: number;
    shopId: string;
    shopName: string;
    shopLogo?: string;
    rating?: number;
    totalReviews?: number;
    isOnSale?: boolean;
    discountPercentage?: number;
    isFavorite?: boolean;
    isInCart?: boolean;
  }