// src/app/components/shop-creation/shop-creation.component.ts
import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ShopConfirmationData } from '../../../interfaces/shop-types';

@Component({
  selector: 'app-shop-creation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './shop-creation.html',
  styleUrls: ['./shop-creation.css']
})
export class ShopCreationComponent {
  @Output() shopDataReady = new EventEmitter<ShopConfirmationData>();

  shopForm: FormGroup;
  selectedFile: File | null = null;
  previewUrl: string | null = null;

  constructor(private fb: FormBuilder) {
    this.shopForm = this.fb.group({
      brandName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(1000)]],
      address: ['', [Validators.required, Validators.maxLength(255)]],
      deliveryFee: [0, [Validators.min(0), Validators.max(100)]]
    });
  }

  onSubmit(): void {
    if (this.shopForm.valid) {
      const formData = this.shopForm.value;
      const shopData: ShopConfirmationData = {
        ...formData,
        logoPicture: this.selectedFile,
        logoPictureUrl: this.previewUrl
      };
      
      this.shopDataReady.emit(shopData);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.handleFile(input.files[0]);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    const files = event.dataTransfer?.files;
    if (files && files[0]) {
      this.handleFile(files[0]);
    }
  }

  private handleFile(file: File): void {
    if (this.isValidImageFile(file)) {
      this.selectedFile = file;
      this.createPreview(file);
    } else {
      alert('Veuillez sélectionner un fichier image valide (PNG, JPG, JPEG) de moins de 5MB.');
    }
  }

  private isValidImageFile(file: File): boolean {
    const validTypes = ['image/png', 'image/jpeg', 'image/jpg'];
    const maxSize = 5 * 1024 * 1024; // 5MB
    
    return validTypes.includes(file.type) && file.size <= maxSize;
  }

  private createPreview(file: File): void {
    const reader = new FileReader();
    reader.onload = (e) => {
      this.previewUrl = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }

  removeFile(event: Event): void {
    event.stopPropagation();
    this.selectedFile = null;
    this.previewUrl = null;
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }
}