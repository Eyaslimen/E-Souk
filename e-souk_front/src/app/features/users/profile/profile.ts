import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
interface UserInfo {
  name: string
  email: string
  phone: string
  address: string
}

interface PasswordData {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

interface Shop {
  name: string
  category: string
  followers: string
}

interface Order {
  id: string
  shop: string
  date: string
  amount: string
  status: string
  statusClass: string
}

interface CartItem {
  name: string
  shop: string
  price: string
  image: string
}

interface FavoriteItem {
  name: string
  shop: string
  price: string
  image: string
}

@Component({
  selector: 'app-profile',
  imports: [FormsModule,CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile {

  activeTab = "personnel"
  isEditing = false

  userInfo: UserInfo = {
    name: "Marie Dubois",
    email: "marie.dubois@email.com",
    phone: "+33 6 12 34 56 78",
    address: "123 Rue de la Paix, 75001 Paris",
  }

  passwordData: PasswordData = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  }

  followedShops: Shop[] = [
    { name: "TechStore Pro", category: "Électronique", followers: "2.1k" },
    { name: "Fashion Hub", category: "Mode", followers: "5.8k" },
    { name: "Bio Market", category: "Alimentation", followers: "1.2k" },
  ]

  orders: Order[] = [
    {
      id: "#ORD-001",
      shop: "TechStore Pro",
      date: "15 Jan 2024",
      amount: "€89.99",
      status: "Livré",
      statusClass: "status-delivered",
    },
    {
      id: "#ORD-002",
      shop: "Fashion Hub",
      date: "12 Jan 2024",
      amount: "€45.50",
      status: "En cours",
      statusClass: "status-pending",
    },
    {
      id: "#ORD-003",
      shop: "Bio Market",
      date: "08 Jan 2024",
      amount: "€23.75",
      status: "Livré",
      statusClass: "status-delivered",
    },
  ]

  cartItems: CartItem[] = [
    {
      name: "Écouteurs Bluetooth",
      shop: "TechStore Pro",
      price: "€79.99",
      image: "assets/images/headphones.jpg",
    },
    {
      name: "T-shirt Bio",
      shop: "Fashion Hub",
      price: "€29.99",
      image: "assets/images/tshirt.jpg",
    },
  ]

  favoriteItems: FavoriteItem[] = [
    {
      name: "Smartphone Pro",
      shop: "TechStore Pro",
      price: "€599.99",
      image: "assets/images/smartphone.jpg",
    },
    {
      name: "Robe d'été",
      shop: "Fashion Hub",
      price: "€89.99",
      image: "assets/images/dress.jpg",
    },
    {
      name: "Miel artisanal",
      shop: "Bio Market",
      price: "€15.99",
      image: "assets/images/honey.jpg",
    },
  ]

  shopStats = {
    sales: 1250,
    clients: 89,
    products: 45,
    revenue: "€12,450",
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing
    if (!this.isEditing) {
      // Reset form if canceling
      this.userInfo = { ...this.userInfo }
    }
  }

  saveProfile(): void {
    this.isEditing = false
    // Implement save logic here
    console.log("Saving profile:", this.userInfo)
  }

  changePassword(): void {
    // Implement password change logic here
    console.log("Changing password")
    this.passwordData = {
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    }
  }

  unfollowShop(index: number): void {
    this.followedShops.splice(index, 1)
  }

  removeFromCart(index: number): void {
    this.cartItems.splice(index, 1)
  }

  addToCart(item: FavoriteItem): void {
    const cartItem: CartItem = {
      name: item.name,
      shop: item.shop,
      price: item.price,
      image: item.image,
    }
    this.cartItems.push(cartItem)
  }

  getCartTotal(): string {
    const total = this.cartItems.reduce((sum, item) => {
      const price = Number.parseFloat(item.price.replace("€", ""))
      return sum + price
    }, 0)
    return `€${total.toFixed(2)}`
  }

  viewOrderDetails(order: Order): void {
    console.log("Viewing order details:", order)
  }

  visitShop(shop: Shop): void {
    console.log("Visiting shop:", shop)
  }

  manageShop(): void {
    console.log("Managing shop")
  }

  finalizeOrder(): void {
    console.log("Finalizing order")
  }
}
