// product-details.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ProductsService } from '../../../services/products-service';
import { ActivatedRoute } from '@angular/router';
import { CartService } from '../../../services/cart.service';
import { ToastrNotifications } from '../../../services/toastr-notifications';



@Component({
  selector: 'app-product-details',
  imports: [CommonModule],
  templateUrl: './product-details.html',
  styleUrl: './product-details.css'
})
export class ProductDetailsComponent implements OnInit {
  productDetails: ProductDetailsDto | null = null; // Changé de undefined à null
  selectedAttributes: { [key: string]: string } = {};
  quantity: number = 1;
  isLoading: boolean = true; // Ajouté pour gérer l'état de chargement
    
  constructor(
    private productsService: ProductsService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private cartService: CartService,
     private notification: ToastrNotifications

  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    
    if (id) {
      console.log('ID récupéré:', id); // Debug
      
      this.productsService.getProductDetails(id).subscribe({
        next: (productDetails: ProductDetailsDto) => {
          console.log("Détails du produit reçus:", productDetails);
          this.productDetails = productDetails;
          this.isLoading = false;
          
          // Forcer la détection des changements
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Erreur lors de la récupération des détails:', error);
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      console.error('ID du produit non trouvé dans la route');
      this.isLoading = false;
    }
  }

  // Méthodes avec vérifications de sécurité améliorées
  getAttributeKeys(): string[] {
    if (!this.productDetails || !this.productDetails.availableAttributes) {
      return [];
    }
    return Object.keys(this.productDetails.availableAttributes);
  }

  getAttributeValues(attributeName: string): string[] {
    if (!this.productDetails || !this.productDetails.availableAttributes) {
      return [];
    }
    return this.productDetails.availableAttributes[attributeName] || [];
  }

  onAttributeSelect(attributeName: string, value: string): void {
    this.selectedAttributes[attributeName] = value;
    console.log('Attribut sélectionné:', attributeName, value); // Debug
  }

  isAttributeSelected(attributeName: string, value: string): boolean {
    return this.selectedAttributes[attributeName] === value;
  }

  increaseQuantity(): void {
    this.quantity++;
  }

  decreaseQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  getTotalPrice(): number {
    if (!this.productDetails) {
      return 0;
    }
    return this.productDetails.price * this.quantity;
  }

  canOrder(): boolean {
    if (!this.productDetails || !this.productDetails.availableAttributes) {
      return false;
    }
    
    const requiredAttributes = Object.keys(this.productDetails.availableAttributes);
    return requiredAttributes.every(attr => this.selectedAttributes[attr]);
  }

  getSelectedAttributesEntries(): Array<{key: string, value: string}> {
    return Object.entries(this.selectedAttributes).map(([key, value]) => ({key, value}));
  }

  onOrder(): void {
    if (this.canOrder() && this.productDetails) {
      const orderData = {
        productId: this.productDetails.productId,
        selectedAttributes: this.selectedAttributes,
        quantity: this.quantity,
        totalPrice: this.getTotalPrice()
      };
      console.log('Commande:', orderData);
      alert('Commande enregistrée ! Vérifiez la console pour les détails.');
    }
  }

  ajouterCart() {
    const productId = this.productDetails?.productId;
    const selectedAttributes = this.selectedAttributes;
    const quantity = this.quantity;

    if (productId) {
      this.cartService.addItemToCart(productId, selectedAttributes, quantity).subscribe(
        result => {
          console.log('Produit ajouté au panier:', result);
          this.notification.success('Produit ajouté au panier !');
        },
        error => this.notification.error('Erreur lors de l\'ajout au panier:', error)
      );
    } else {
      this.notification.error('ID du produit non trouvé');
    }
  }

}
