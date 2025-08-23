import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Shop } from '../../../interfaces/shopDetails';
import { ShopService } from '../../../services/shopDetails/shop.service';
import { CommonModule } from '@angular/common';
import { ShopDetails } from '../../../services/shop-details';
import { ShopGeneralDetails } from '../../../interfaces/ProductDetails';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [CommonModule],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header implements OnInit {
  shop$!: Observable<Shop>
  generalDetails$!: Observable<ShopGeneralDetails>;
  @Input() name!: string;
  constructor(private shopService: ShopService,private shopDetailsService: ShopDetails  ) {}

  ngOnInit(): void {
    this.shop$ = this.shopService.getShopData()
    this.generalDetails$ = this.shopDetailsService.getShopDetails(this.name);
    console.log(this.generalDetails$);
  }

  getStarArray(rating: number): number[] {
    return Array(5)
      .fill(0)
      .map((_, i) => (i < Math.floor(rating) ? 1 : 0))
  }

  getOwnerInitials(ownerName: string): string {
    if (!ownerName) return '';
    return ownerName.split(' ').map(name => name.charAt(0)).join('');
  }
}
