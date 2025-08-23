import { Injectable } from "@angular/core"
import { BehaviorSubject, type Observable } from "rxjs"
import { Boutique, Stats } from "../../interfaces/shopDetails"

@Injectable({
  providedIn: "root",
})
export class BoutiqueService {
  private boutiqueData: Boutique = {
    name: "EvaShop",
    ownerName: "Sophie Martin",
    description:
      "Créatrice passionnée de bijoux artisanaux et d'accessoires uniques. Chaque pièce est fabriquée à la main avec amour et attention aux détails.",
    location: "Paris, France",
    category: "Bijoux & Accessoires",
    productsCount: 47,
    followersCount: 1250,
    rating: 4.8,
    maxRating: 5,
  }

  private statsData: Stats = {
    products: 47,
    subscribers: 1250,
    orders: 380,
  }

  getBoutique(): Observable<Boutique> {
    return new BehaviorSubject(this.boutiqueData).asObservable()
  }

  getStats(): Observable<Stats> {
    return new BehaviorSubject(this.statsData).asObservable()
  }
}
