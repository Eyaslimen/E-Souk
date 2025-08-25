import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Shop } from '../../interfaces/Shop';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-shop-card',
  imports: [CommonModule],
  templateUrl: './shop-card.html',
  styleUrl: './shop-card.css'
})
export class ShopCard {
  @Input() shop!: Shop
  @Input() isFollowed = false

  @Output() viewDetails = new EventEmitter<void>()
  @Output() toggleFollowEvent = new EventEmitter<void>()

  onViewDetails(event?: Event): void {
    if (event) {
      event.stopPropagation()
    }
    this.viewDetails.emit()
  }

  toggleFollow(event: Event): void {
    event.stopPropagation()
    this.isFollowed = !this.isFollowed
    this.toggleFollowEvent.emit()
  }

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
