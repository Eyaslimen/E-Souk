// src/app/components/onboarding/onboarding.component.ts (Version corrigée)
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ShopCreationComponent } from '../shop-creation/shop-creation';
import { ShopConfirmationModalComponent } from '../shop-confirmation-modal/shop-confirmation-modal';
import { ProductCreationComponent } from '../../products/product-creation/product-creation';
import { 
  OnboardingState, 
  OnboardingStep, 
  ShopConfirmationData, 
  CreateShopRequestDTO,
  ProductSummary 
} from '../../../interfaces/shop-types';
import { OnboardingService } from '../../../services/onboarding';

@Component({
  selector: 'app-onboarding',
  standalone: true,
  imports: [
    CommonModule,
    ShopCreationComponent,
    ShopConfirmationModalComponent,
    ProductCreationComponent
  ],
  templateUrl: './onboarding.html',
  styleUrl: './onboarding.css'
})
export class OnboardingComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  currentState!: OnboardingState;
  OnboardingStep = OnboardingStep;
  
  showConfirmationModal = false;
  pendingShopData: ShopConfirmationData | null = null;
  isCreatingShop = false;

  constructor(
    private onboardingService: OnboardingService, // CORRECTION: Nom du service
    private router: Router
  ) {}

  ngOnInit(): void {
    // CORRECTION: Ajout de logs pour débugger
    console.log('🚀 Initialisation du composant onboarding');
    
    this.onboardingService.onboardingState$
      .pipe(takeUntil(this.destroy$))
      .subscribe(state => {
        console.log('📊 Nouvel état reçu dans le composant:', state);
        console.log('🎯 Current step:', state.currentStep);
        console.log('🏪 Shop ID:', state.shopId);
        this.currentState = state;
        // CORRECTION: Log pour vérifier quelle section va s'afficher
        this.debugCurrentDisplay();
      });
    
    // CORRECTION: Debug de l'état initial
    setTimeout(() => {
      this.onboardingService.debugCurrentState();
    }, 100);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // CORRECTION: Méthode de debug pour savoir quelle section s'affiche
  private debugCurrentDisplay(): void {
    const step = this.currentState?.currentStep;
    const shopId = this.currentState?.shopId;
    console.log(' Section qui va s\'afficher:');
    console.log(`   - CREATE_SHOP: ${step === OnboardingStep.CREATE_SHOP}`);
    console.log(`   - ADD_PRODUCTS: ${step === OnboardingStep.ADD_PRODUCTS && shopId}`);
    console.log(`   - COMPLETED: ${step === OnboardingStep.COMPLETED}`);
    console.log(`   - Modal confirmation: ${this.showConfirmationModal}`);
  }

  onShopDataReady(shopData: ShopConfirmationData): void {
    console.log('📝 Données de boutique reçues:', shopData);
    this.pendingShopData = shopData;
    this.showConfirmationModal = true;
  }

  modifyShopData(): void {
    console.log('✏️ Modification des données de boutique');
    this.showConfirmationModal = false;
    this.pendingShopData = null;
  }

  cancelShopCreation(): void {
    console.log('❌ Annulation de la création de boutique');
    this.showConfirmationModal = false;
    this.pendingShopData = null;
  }

  // CORRECTION: Refactorisation complète de la méthode de confirmation
  async confirmShopCreation(): Promise<void> {
    if (!this.pendingShopData) {
      console.error('❌ Pas de données de boutique en attente');
      return;
    }

    console.log('🔄 Début de la création de boutique...');
    this.isCreatingShop = true;

    try {
      const shopRequestData: CreateShopRequestDTO = {
        brandName: this.pendingShopData.brandName,
        description: this.pendingShopData.description,
        deliveryFee: this.pendingShopData.deliveryFee,
        address: this.pendingShopData.address,
        logoPicture: this.pendingShopData.logoPicture
      };

      console.log('📤 Envoi des données:', shopRequestData);

      // CORRECTION: Utilisation de firstValueFrom au lieu de toPromise (deprecated)
      const response = await new Promise<{ id: string }>((resolve, reject) => {
        this.onboardingService.createShop(shopRequestData).subscribe({
          next: (res) => {
            console.log('✅ Réponse reçue:', res);
            resolve(res);
          },
          error: (err) => {
            console.error('❌ Erreur lors de la création:', err);
            reject(err);
          }
        });
      });

      if (response?.id) {
        console.log('🎉 Boutique créée avec succès, ID:', response.id);
        
        // CORRECTION: Fermeture immédiate et forcée du modal
        this.showConfirmationModal = false;
        this.pendingShopData = null;
        this.isCreatingShop = false; // IMPORTANT: Reset ici aussi
        
        console.log('✅ Modal fermé et données nettoyées');
        
        // CORRECTION: Vérification que l'état a bien été mis à jour
        setTimeout(() => {
          const currentState = this.onboardingService.getCurrentState();
          console.log('🔍 Vérification de l\'état après création:', currentState);
          
          if (currentState.currentStep !== OnboardingStep.ADD_PRODUCTS) {
            console.warn('⚠️ L\'étape n\'a pas changé, forçage...');
            this.onboardingService.goToStep(OnboardingStep.ADD_PRODUCTS);
          }
          
          // CORRECTION: Force la détection de changement
          this.debugCurrentDisplay();
        }, 100);
        
      } else {
        console.error('❌ Réponse invalide:', response);
        alert('Erreur: Réponse invalide du serveur');
      }
      
    } catch (error) {
      console.error('❌ Erreur lors de la création de la boutique:', error);
      alert(`Une erreur est survenue lors de la création de la boutique: ${error}`);
    } finally {
      this.isCreatingShop = false;
      console.log('🏁 Fin de la création de boutique');
    }
  }

  onProductAdded(product: ProductSummary): void {
    console.log('✅ Produit ajouté:', product);
    // Le service gère déjà la mise à jour de l'état
  }

  onOnboardingComplete(): void {
    console.log('🎉 Onboarding terminé');
    this.onboardingService.updateOnboardingState({
      currentStep: OnboardingStep.COMPLETED
    });
  }

  getTotalVariants(): number {
    return this.currentState?.products?.reduce((total, product) => total + product.variantCount, 0) || 0;
  }

  goToShop(): void {
    console.log('🏪 Redirection vers la boutique');
    this.router.navigate(['/']);
  }

  // CORRECTION: Méthodes de debug pour le template
  debugStep(): void {
    console.log('🔍 Debug step depuis template:', {
      currentStep: this.currentState?.currentStep,
      CREATE_SHOP: OnboardingStep.CREATE_SHOP,
      ADD_PRODUCTS: OnboardingStep.ADD_PRODUCTS,
      COMPLETED: OnboardingStep.COMPLETED,
      shopId: this.currentState?.shopId
    });
  }

  // CORRECTION: Getter pour vérifier l'affichage des sections
  get shouldShowCreateShop(): boolean {
    const result = this.currentState?.currentStep === OnboardingStep.CREATE_SHOP;
    console.log('🔍 shouldShowCreateShop:', result);
    return result;
  }

  get shouldShowAddProducts(): boolean {
    const hasValidStep = this.currentState?.currentStep === OnboardingStep.ADD_PRODUCTS;
    const hasShopId = !!this.currentState?.shopId && this.currentState.shopId.trim().length > 0;
    const result = hasValidStep && hasShopId;
    
    console.log('🔍 shouldShowAddProducts:', result, {
      step: this.currentState?.currentStep,
      stepEnum: OnboardingStep.ADD_PRODUCTS,
      shopId: this.currentState?.shopId,
      hasValidStep,
      hasShopId
    });
    return result;
  }

  get shouldShowCompleted(): boolean {
    const result = this.currentState?.currentStep === OnboardingStep.COMPLETED;
    console.log('🔍 shouldShowCompleted:', result);
    return result;
  }
}