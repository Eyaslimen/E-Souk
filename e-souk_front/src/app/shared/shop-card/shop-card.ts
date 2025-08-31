import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Shop } from '../../interfaces/Shop';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ShopFollowrsService } from '../../services/shop-followers.service';
@Component({
  selector: 'app-shop-card',
  imports: [CommonModule,RouterModule],
  templateUrl: './shop-card.html',
  styleUrl: './shop-card.css'
})
export class ShopCard {
  @Input() shop!: Shop
  @Input() isFollowed = false

  @Output() viewDetails = new EventEmitter<void>()
  @Output() toggleFollowEvent = new EventEmitter<void>()

  constructor(private router: Router, private shopFollowersService: ShopFollowrsService) {}
onViewDetails(event?: Event): void {
  if (event) {
    event.preventDefault();
    event.stopPropagation();
  }
  this.router.navigate(['/shops', this.shop.brandName]);
  this.viewDetails.emit();
}

  ajouterAuxFavoris() {
    const shopId = this.shop.id;
    this.shopFollowersService.followShop(shopId).subscribe(
      result => {
        console.log('Boutique suivie:', result);
        this.isFollowed = true;
      },
      error => console.error('Erreur:', error)
    );
  }
  // toggleFollow(event: Event): void {
  //   event.stopPropagation()
  //   this.isFollowed = !this.isFollowed
  //   this.toggleFollowEvent.emit()
  // }

  formatFollowers(count: number): string {
    if (count >= 1000) {
      return (count / 1000).toFixed(1) + "k"
    }
    return count.toString()
  }

  getInitials(name: string): string {
    return name.charAt(0).toUpperCase()
  }
}
