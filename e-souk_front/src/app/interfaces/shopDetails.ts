export interface Shop {
  id: string
  name: string
  ownerName: string
  bio: string
  address: string
  category: string
  productsCount: number
  followersCount: number
  rating: number
  maxRating: number
  avatar: string
  coverImage?: string
}

export interface Product {
  id: string
  name: string
  price: number
  originalPrice?: number
  image: string
  category: string
  rating: number
  reviewsCount: number
  isNew?: boolean
  isPopular?: boolean
  soldCount?: number
  createdAt: Date
}

export interface Review {
  id: string
  customerName: string
  rating: number
  comment: string
  date: Date
  isVerified: boolean
}

export interface ShopStats {
  productsCount: number
  followersCount: number
  ordersCount: number
}

export interface ContactInfo {
  email: string
  phone: string
}
export interface Boutique {
  name: string
  ownerName: string
  description: string
  location: string
  category: string
  productsCount: number
  followersCount: number
  rating: number
  maxRating: number
}

export interface Product {
  id: string
  name: string
  price: number
  originalPrice?: number
  rating: number
  reviewsCount: number
  category: string
  image: string
  isNew?: boolean
  isPopular?: boolean
  soldCount?: number
}

export interface Review {
  id: string
  customerName: string
  rating: number
  comment: string
  date: Date
  isVerified: boolean
  helpful?: number
}

export interface Stats {
  products: number
  subscribers: number
  orders: number
}
