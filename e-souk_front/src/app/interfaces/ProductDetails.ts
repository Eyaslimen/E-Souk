export interface ShopGeneralDetails {
    brandName: string;
    bio: string;
    description: string;
    logoPicture: string;
    deliveryFee: number;
    address: string;
    createdAt: string; // ISO date string
    ownerName: string;
    ownerPicture: string;
    productCount: number;
    followerCount: number;
    categoryName: string;
    phone: string;
    instagramLink: string;
    facebookLink: string;
}

export interface Review {
    id: string; // UUID as string
    shopName: string;
    content: string;
    author: string;
    rating: number; // 1 to 5
    createdAt: string; // ISO date string
    reviewType?: string; // You may define an enum if needed
}
export interface ReviewRequest {
    content:string;
    rating: number;
    reviewType?: string;
}

export interface ProductDetails {
    id: string; // UUID as string
    name: string;
    description: string;
    price: number;
    picture: string;
    categoryName: string;
    shopName: string;
}
