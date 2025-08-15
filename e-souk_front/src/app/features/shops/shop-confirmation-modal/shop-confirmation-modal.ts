// src/app/components/shop-confirmation-modal/shop-confirmation-modal.component.ts
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShopConfirmationData } from '../../../interfaces/shop-types';

@Component({
  selector: 'app-shop-confirmation-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './shop-confirmation-modal.html',
  styleUrls: ['./shop-confirmation-modal.css']
})
export class ShopConfirmationModalComponent {
  @Input() isVisible = false;
  @Input() shopData!: ShopConfirmationData;
  @Input() isLoading = false;
  
  @Output() confirm = new EventEmitter<void>();
  @Output() modify = new EventEmitter<void>();
  @Output() close = new EventEmitter<void>();

  onOverlayClick(event: Event): void {
    this.close.emit();
  }

  onConfirm(): void {
    this.confirm.emit();
  }

  onModify(): void {
    this.modify.emit();
  }
}