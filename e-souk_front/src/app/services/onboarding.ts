// src/app/services/onboarding.service.ts (Version corrigée)
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { 
  CreateShopRequestDTO, 
  CreateProductRequestDTO, 
  OnboardingState, 
  OnboardingStep,
  ProductSummary 
} from '../interfaces/shop-types';

@Injectable({
  providedIn: 'root'
})
export class OnboardingService {
  private readonly API_BASE = 'http://localhost:8080/api';
  
  private onboardingStateSubject = new BehaviorSubject<OnboardingState>({
    currentStep: OnboardingStep.CREATE_SHOP,
    products: []
  });

  public onboardingState$ = this.onboardingStateSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Gestion de l'état
  updateOnboardingState(updates: Partial<OnboardingState>): void {
    console.log('🔄 Mise à jour de l\'état d\'onboarding:', updates);
    const currentState = this.onboardingStateSubject.value;
    const newState = { ...currentState, ...updates };
    
    console.log('📊 État actuel:', currentState);
    console.log('🆕 Nouvel état:', newState);
    console.log('🎯 Step actuel:', newState.currentStep);
    console.log('🏪 ShopId:', newState.shopId);
    
    this.onboardingStateSubject.next(newState);
  }

  getCurrentState(): OnboardingState {
    return this.onboardingStateSubject.value;
  }

  // Création de boutique
  createShop(shopData: CreateShopRequestDTO): Observable<{ id: string }> {
    const formData = new FormData();
    formData.append('brandName', shopData.brandName);
    if (shopData.bio) {
      formData.append('description', shopData.bio);
    }
    if (shopData.categoryName) {
    formData.append('categoryName',shopData.categoryName); 
        }
    formData.append('address', shopData.address);
    formData.append('phone', shopData.phone);

    if (shopData.instagramLink) {
      formData.append('instagramLink', shopData.instagramLink);
    }
        if (shopData.facebookLink) {
      formData.append('facebookLink', shopData.facebookLink);
    }

    formData.append('deliveryFee', shopData.deliveryFee.toString());

    if (shopData.logoPicture) {
      formData.append('logoPicture', shopData.logoPicture);
    }

    return this.http.post<{ id: string }>(`${this.API_BASE}/shops`, formData)
      .pipe(
        tap(response => {
          console.log('✅ Boutique créée avec succès:', response);
          console.log('🔑 ID de la boutique:', response.id);
          
          this.updateOnboardingState({
            shopId: response.id,
            shopData: shopData,
            currentStep: OnboardingStep.ADD_PRODUCTS
          });
          
          console.log('🚀 État mis à jour - passage à ADD_PRODUCTS');
        }),
        catchError(this.handleError)
      );
  }

  // CORRECTION: Ajout de produit avec le bon format pour le backend
  addProduct(productData: CreateProductRequestDTO): Observable<any> {
    console.log("🚀 Envoi du produit au backend:", productData);
    
    const formData = new FormData();
    
    // Champs de base - CORRECTION: utiliser categoryName au lieu de category
    formData.append('name', productData.name);
    formData.append('categoryName', productData.category); // ✅ Changé de 'category' vers 'categoryName'
    
    if (productData.description) {
      formData.append('description', productData.description);
    }

    // CORRECTION: Transformer les attributs au format attendu par le backend
    const backendAttributes = productData.attributes.map(attr => ({
      name: attr.name,
      values: attr.values
    }));
    formData.append('attributesJson', JSON.stringify(backendAttributes));

    // CORRECTION: Transformer les variantes au format attendu par le backend
    const backendVariants = productData.variants.map(variant => ({
      attributeValues: Object.entries(variant.attributes).map(([attributeName, value]) => ({
        attributeName,
        value
      })),
      price: variant.price,
      stock: variant.stock
    }));
    formData.append('variantsJson', JSON.stringify(backendVariants));

    // CORRECTION: Ajouter les images si elles existent
    if (productData.images && productData.images.length > 0) {
      productData.images.forEach((image) => {
        formData.append('imageUrl', image); // ✅ Changé de 'images' vers 'imageUrl'
      });
    }

    // CORRECTION: Utiliser le shopId comme paramètre de requête
    const url = `${this.API_BASE}/products/add?shopId=${productData.shopId}`;

    console.log('📡 URL de la requête:', url);
    console.log('📦 FormData préparé:');
    
    // Debug FormData
    formData.forEach((value, key) => {
      console.log(`  ${key}:`, value);
    });

    return this.http.post<any>(url, formData)
      .pipe(
        tap(response => {
          console.log('✅ Produit ajouté avec succès:', response);
          
          // Créer un ProductSummary à partir de la réponse
          const productSummary: ProductSummary = {
            id: response.id || Date.now().toString(),
            name: productData.name,
            category: productData.category,
            variantCount: productData.variants.length,
            totalStock: productData.variants.reduce((sum, v) => sum + v.stock, 0),
            minPrice: Math.min(...productData.variants.map(v => v.price)),
            maxPrice: Math.max(...productData.variants.map(v => v.price))
          };

          const currentProducts = this.getCurrentState().products || [];
          this.updateOnboardingState({
            products: [...currentProducts, productSummary]
          });
        }),
        catchError((error) => {
          console.error('❌ Erreur lors de l\'ajout du produit:', error);
          return this.handleError(error);
        })
      );
  }

  // Méthodes utilitaires
  resetOnboarding(): void {
    console.log('🔄 Reset de l\'onboarding');
    this.onboardingStateSubject.next({
      currentStep: OnboardingStep.CREATE_SHOP,
      products: []
    });
  }

  goToStep(step: OnboardingStep): void {
    console.log('🎯 Changement forcé d\'étape vers:', step);
    this.updateOnboardingState({
      currentStep: step
    });
  }

  goToNextStep(): void {
    const currentState = this.getCurrentState();
    console.log('➡️ Passage à l\'étape suivante depuis:', currentState.currentStep);
    
    if (currentState.currentStep < OnboardingStep.COMPLETED) {
      const nextStep = (currentState.currentStep + 1) as OnboardingStep;
      console.log('🎯 Prochaine étape:', nextStep);
      this.updateOnboardingState({
        currentStep: nextStep
      });
    }
  }

  goToPreviousStep(): void {
    const currentState = this.getCurrentState();
    if (currentState.currentStep > OnboardingStep.CREATE_SHOP) {
      this.updateOnboardingState({
        currentStep: (currentState.currentStep - 1) as OnboardingStep
      });
    }
  }

  // Méthodes de validation
  isOnboardingComplete(): boolean {
    const state = this.getCurrentState();
    return state.currentStep === OnboardingStep.COMPLETED && 
           state.shopId !== undefined && 
           (state.products?.length || 0) > 0;
  }

  getProgressPercentage(): number {
    const state = this.getCurrentState();
    const baseProgress = (state.currentStep - 1) * 50;
    
    if (state.currentStep === OnboardingStep.ADD_PRODUCTS) {
      const productsAdded = state.products?.length || 0;
      const productBonus = Math.min(productsAdded * 10, 40);
      return Math.min(baseProgress + productBonus, 100);
    }
    
    return baseProgress;
  }

  debugCurrentState(): void {
    const state = this.getCurrentState();
    console.log('🔍 DEBUG - État actuel complet:', {
      currentStep: state.currentStep,
      shopId: state.shopId,
      products: state.products,
      shopData: state.shopData
    });
  }

  // Gestion centralisée des erreurs
  private handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage = 'Une erreur inattendue s\'est produite.';
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur client: ${error.error.message}`;
    } else {
      switch (error.status) {
        case 400:
          errorMessage = 'Données invalides. Veuillez vérifier les informations saisies.';
          break;
        case 401:
          errorMessage = 'Vous n\'êtes pas autorisé à effectuer cette action.';
          break;
        case 403:
          errorMessage = 'Accès refusé. Veuillez vous connecter.';
          break;
        case 404:
          errorMessage = 'Ressource non trouvée.';
          break;
        case 409:
          errorMessage = 'Une boutique avec ce nom existe déjà.';
          break;
        case 413:
          errorMessage = 'Les fichiers sont trop volumineux.';
          break;
        case 422:
          errorMessage = 'Données non valides. Veuillez corriger les erreurs.';
          break;
        case 500:
          errorMessage = 'Erreur interne du serveur. Veuillez réessayer plus tard.';
          break;
        default:
          errorMessage = `Erreur ${error.status}: ${error.message}`;
      }
    }
    
    console.error('❌ Erreur API:', error);
    console.error('📝 Détails de l\'erreur:', error.error);
    return throwError(() => new Error(errorMessage));
  }

  // Méthodes pour la persistance locale
  saveToLocalStorage(): void {
    try {
      const state = this.getCurrentState();
      const stateToSave = {
        ...state,
        shopData: state.shopData ? {
          ...state.shopData,
          logoPicture: undefined
        } : undefined
      };
      localStorage.setItem('onboarding_state', JSON.stringify(stateToSave));
    } catch (error) {
      console.warn('Impossible de sauvegarder l\'état dans localStorage:', error);
    }
  }

  loadFromLocalStorage(): void {
    try {
      const saved = localStorage.getItem('onboarding_state');
      if (saved) {
        const state = JSON.parse(saved);
        if (this.isValidState(state)) {
          this.onboardingStateSubject.next(state);
        }
      }
    } catch (error) {
      console.warn('Impossible de charger l\'état depuis localStorage:', error);
    }
  }

  clearLocalStorage(): void {
    try {
      localStorage.removeItem('onboarding_state');
    } catch (error) {
      console.warn('Impossible de vider localStorage:', error);
    }
  }

  private isValidState(state: any): boolean {
    return state && 
           typeof state.currentStep === 'number' && 
           state.currentStep >= OnboardingStep.CREATE_SHOP && 
           state.currentStep <= OnboardingStep.COMPLETED &&
           Array.isArray(state.products);
  }
}