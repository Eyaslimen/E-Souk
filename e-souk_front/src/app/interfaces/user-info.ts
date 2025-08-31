   export interface UserProfile {
       id?: String;
       username?: String;
       email?: String;
       picture?: String;
       phone?: String;
       address?: String;
       codePostal?: String;
       isActive?: Boolean;
       joinedAt?: Date;
       updatedAt?: Date;
   }
export interface CartDto {
    shopCarts: ShopCartDto[];
    totalPrice: number;
    totalItems: number;
    shopCount: number;
}

export interface ProductDTO {
    id: String;
    name: String;
    description: String;
    price: number;
    picture: String;
    categoryName: String;
    shopName: String;
}
export interface ShopSummaryDTO {
    id: String;
    brandName: String;
    description: String;
    logoPicture: String;
    ownerUsername: String;
    categoryName: String;
    address: String;
    productCount: number;
    followerCount: number;
} 

export interface ShopCartDto {
    
    shopId: String;

    shopName: String;

    items: CartItemDto[];

    shopTotal: number;

    itemCount: number;
}
export interface CartItemDto {

    id: String;
    name: String;
    price: number;
    picture: String;
    shopName: String;
    selectedAttributes: Map<String, String>;
    quantity: number;
} 

// interfaces/cart.interface.ts
export interface CartItem {
  id: string;
  name: string;
  price: number;
  picture: string;
  shopName: string;
  selectedAttributes: Record<string, string>;
  quantity: number;
}

export interface ShopCart {
  shopId: string;
  shopName: string;
  items: CartItem[];
  shopTotal: number;
  itemCount: number;
}

export interface Cart {
  shopCarts: ShopCart[];
  totalPrice: number;
  totalItems: number;
  shopCount: number;
}

// orders 
export interface UserOrdersDto {
    shopOrders: ShopOrdersDto[];
    totalOrders: number;
    shopCount: number;
}
export interface ShopOrdersDto {
    shopId: String;
    shopName: String;
    orders: CommandeDTO[];
    orderCount: number;
}
export interface CommandeDTO {

    id: String;
    orderNumber: String;
    userId: String;
    customerName: String;
    shopId: String;
    shopName: String;
    deliveryAddress: String;
    deliveryPostalCode: String;
    total: number;
    deliveryFee: number;
    subtotal: number;
    etat: EtatCommande;
    totalItemCount: number;
    uniqueItemCount: number;
    createdAt: Date;
    shippedAt: Date;
    deliveredAt: Date;
    orderItems: OrderItemDTO[];
}
export interface OrderItemDTO {

    id: String;
    commandeId: String;
    variantId: String;
    productId: String;
    productName: String;
    productImage: String;
    variantName: String;
    price: number;
    quantity: number;
    subTotal: number;
    shopName: String;
    shopId: String;
}

export enum EtatCommande {
    EnAttente,
    EnCours,
    Expediee,
    Livree,
    Annulee
} 
