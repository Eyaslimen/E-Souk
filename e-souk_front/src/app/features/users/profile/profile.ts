import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Cart, CartItem, EtatCommande, ProductDTO, ShopCart, ShopSummaryDTO, UserOrdersDto, UserProfile } from '../../../interfaces/user-info';
import { UserInfoService } from '../../../services/user-info.service';
import { ShopFollowrsService } from '../../../services/shop-followers.service';
import { CommandeService } from '../../../services/commande.service';
import { ShopCard } from '../../../shared/shop-card/shop-card';
import { ToastrNotifications } from '../../../services/toastr-notifications';
import { AuthService } from '../../../services/AuthService';
import { ActivatedRoute, Router } from '@angular/router';
interface PasswordData {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}



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
    private commandeService: CommandeService,
    private authService: AuthService,
    private router : Router,
    private route : ActivatedRoute,
    private notification : ToastrNotifications
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
   this.route.queryParams.subscribe(params => {
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });
  }

logout() : void {
  console.log("essai de deconnecion");
  this.authService.logout();
  this.notification.success("Déconnexion effectuée, à bientôt!")
  this.router.navigate(['/accueil']);
  
}


  passwordData: PasswordData = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
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
          console.log('Commande bien passée:', result);
          this.notification.success("Commande passée!");
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


  // Orders 
   formatDate(date: Date): string {
    if (!date) return '-';
    const d = new Date(date);
    return d.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  getStatusClass(etat: EtatCommande): string {
    switch (etat) {
      case EtatCommande.EnAttente:
        return 'status-en-attente';
      case EtatCommande.EnCours:
        return 'status-en-cours';
      case EtatCommande.Expediee:
        return 'status-expediee';
      case EtatCommande.Livree:
        return 'status-livree';
      case EtatCommande.Annulee:
        return 'status-annulee';
      default:
        return 'status-en-attente';
    }
  }

  getStatusText(etat: EtatCommande): string {
    switch (etat) {
      case EtatCommande.EnAttente:
        return 'En attente';
      case EtatCommande.EnCours:
        return 'En cours';
      case EtatCommande.Expediee:
        return 'Expédiée';
      case EtatCommande.Livree:
        return 'Livrée';
      case EtatCommande.Annulee:
        return 'Annulée';
      default:
        return 'En attente';
    }
  }
}