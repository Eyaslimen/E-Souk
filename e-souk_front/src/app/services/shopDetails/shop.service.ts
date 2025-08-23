import { Injectable } from "@angular/core"
import { BehaviorSubject, type Observable } from "rxjs"
import { ContactInfo, Shop, ShopStats } from "../../interfaces/shopDetails"

@Injectable({
  providedIn: "root",
})
export class ShopService {
  private shopData: Shop = {
    id: "evashop-001",
    name: "EvaShop",
    ownerName: "Sophie Martin",
    bio: "Créatrice passionnée de bijoux artisanaux et d'accessoires uniques. Chaque pièce est fabriquée à la main avec amour et attention aux détails.",
    address: "Paris, France",
    category: "Bijoux & Accessoires",
    productsCount: 47,
    followersCount: 1250,
    rating: 4.8,
    maxRating: 5,
    avatar: "/sophie-martin-avatar.png",
    coverImage: "/evashop-cover.png",
  }

  private stats: ShopStats = {
    productsCount: 47,
    followersCount: 1250,
    ordersCount: 380,
  }

  private contact: ContactInfo = {
    email: "contact@evashop.com",
    phone: "+33 1 23 45 67 89",
  }

  getShopData(): Observable<Shop> {
    return new BehaviorSubject(this.shopData).asObservable()
  }

  getStats(): Observable<ShopStats> {
    return new BehaviorSubject(this.stats).asObservable()
  }

  getContact(): Observable<ContactInfo> {
    return new BehaviorSubject(this.contact).asObservable()
  }
}
