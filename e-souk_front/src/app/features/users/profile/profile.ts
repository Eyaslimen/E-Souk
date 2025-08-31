import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Cart, CartItem, ProductDTO, ShopCart, ShopSummaryDTO, UserOrdersDto, UserProfile } from '../../../interfaces/user-info';
import { UserInfoService } from '../../../services/user-info.service';
import { ShopFollowrsService } from '../../../services/shop-followers.service';
import { CommandeService } from '../../../services/commande.service';
import { ShopCard } from '../../../shared/shop-card/shop-card';
interface PasswordData {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

// interface Shop {
//   name: string
//   category: string
//   followers: string
// }

interface Order {
  id: string
  shop: string
  date: string
  amount: string
  status: string
  statusClass: string
}


// interface FavoriteItem {
//   name: string
//   shop: string
//   price: string
//   image: string
// }

@Component({
  selector: 'app-profile',
  imports: [FormsModule,CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {
  userInfo: UserProfile | null = null;
  productsFavoris : ProductDTO[] = [];
  followedShops: ShopSummaryDTO[] = [];
  userOrders : UserOrdersDto | null = null;
  activeTab = "personnel"
  isEditing = false;
  cart: Cart | null = null;
  loading = false;
  constructor(private UserInfoService: UserInfoService,
    private shopFollowersService: ShopFollowrsService,
    private commandeService: CommandeService
  ) {}

  ngOnInit(): void {
    this.UserInfoService.getUserProfile().subscribe((userInfo: UserProfile | null) => {
      this.userInfo = userInfo;
      console.log("Données utilisateur reçues:", this.userInfo);
    });
    this.UserInfoService.getUserFavorites().subscribe((favorites: ProductDTO[]) => {
      this.productsFavoris = favorites;
      console.log("Produits favoris reçus:", this.productsFavoris);
    });
    this.UserInfoService.getUserFollowedShops().subscribe((shops: ShopSummaryDTO[]) => {
      this.followedShops = shops;
      console.log("Boutiques suivies reçues:", this.followedShops);
    });
    this.UserInfoService.getCart().subscribe((user_cart:Cart) => {
      this.cart=user_cart;
      console.log("cart recupéerée",this.cart);
    });
    this.UserInfoService.getOrders().subscribe((User_Orders:UserOrdersDto) =>
    {
      this.userOrders=User_Orders;
      console.log(this.userOrders);
    }  
  );
  }

  // userInfo: UserInfo = {
  //   name: "Marie Dubois",
  //   email: "marie.dubois@email.com",
  //   phone: "+33 6 12 34 56 78",
  //   address: "123 Rue de la Paix, 75001 Paris",
  // }

  passwordData: PasswordData = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  }

  // followedShops: Shop[] = [
  //   { name: "TechStore Pro", category: "Électronique", followers: "2.1k" },
  //   { name: "Fashion Hub", category: "Mode", followers: "5.8k" },
  //   { name: "Bio Market", category: "Alimentation", followers: "1.2k" },
  // ]

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


  // favoriteItems: FavoriteItem[] = [
  //   {
  //     name: "Smartphone Pro",
  //     shop: "TechStore Pro",
  //     price: "€599.99",
  //     image: "assets/images/smartphone.jpg",
  //   },
  //   {
  //     name: "Robe d'été",
  //     shop: "Fashion Hub",
  //     price: "€89.99",
  //     image: "assets/images/dress.jpg",
  //   },
  //   {
  //     name: "Miel artisanal",
  //     shop: "Bio Market",
  //     price: "€15.99",
  //     image: "assets/images/honey.jpg",
  //   },
  // ]

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
    // if (!this.isEditing) {
    //   // Reset form if canceling
    //   this.userInfo = { ...this.userInfo }
    // }
  }

  saveProfile(): void {
    this.isEditing = false
    // Implement save logic here
    console.log("Saving userInfo:", this.userInfo)
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
    const id = this.followedShops[index].id;
      this.followedShops.splice(index, 1)
    console.log("Unfollowed shop:", id)
    // Implement unfollow logic here
    this.shopFollowersService.unFollowShop(id).subscribe(() => {
      console.log("Unfollowed shop:", id)
    })
  }

  orderShop(shop: ShopCart) {
    console.log("essayer de passer une commande",shop)
    const shopId=shop.shopId;
    console.log(shopId);
      if (shopId) {
      this.commandeService.addOrder(shopId).subscribe(
        result => {
          console.log('Produit ajouté au panier:', result);
          alert('Produit ajouté au panier !');
        },
        error => console.error('Erreur lors de l\'ajout au panier:', error)
      );
    } else {
      console.error('ID du produit non trouvé');
    }
  }

  hasAttributes(item: CartItem): boolean {
    return Object.keys(item.selectedAttributes).length > 0;
  }

  getAttributes(item: CartItem): Array<{key: string, value: string}> {
    return Object.entries(item.selectedAttributes).map(([key, value]) => ({key, value}));
  }



  // id: String;
  //   name: String;
  //   description: String;
  //   price: number;
  //   picture: String;
  //   categoryName: String;
  //   shopName: String;
  // getCartTotal(): string {
  //   const total = this.cartItems.reduce((sum, item) => {
  //     const price = Number.parseFloat(item.price.replace("€", ""))
  //     return sum + price
  //   }, 0)
  //   return `€${total.toFixed(2)}`
  // }

  viewOrderDetails(order: Order): void {
    console.log("Viewing order details:", order)
  }

  visitShop(shop: ShopSummaryDTO): void {
    console.log("Visiting shop:", shop)
  }

  manageShop(): void {
    console.log("Managing shop")
  }

  finalizeOrder(): void {
    console.log("Finalizing order")
  }
    addToCart(item: ProductDTO): void {
    const cartItem: ProductDTO = {
      id: item.id,
      name: item.name,
      description:item.description,
      shopName: item.shopName,
      price: item.price,
      categoryName:item.categoryName,
      picture: item.picture,
    }
  }
}