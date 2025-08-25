// src/app/components/product-creation/product-creation.component.ts
import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray, FormsModule } from '@angular/forms';
import { ProductAttribute, ProductVariant, CreateProductRequestDTO, ProductSummary } from '../../../interfaces/shop-types';
import { OnboardingService } from '../../../services/onboarding';

@Component({
  selector: 'app-product-creation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './product-creation.html',
  styleUrls: ['./product-creation.css']
})
export class ProductCreationComponent {
  @Input() shopId!: string;
  @Output() productAdded = new EventEmitter<ProductSummary>();
  @Output() onboardingComplete = new EventEmitter<void>();

  // ✅ AJOUT: Injection du service
  private onboardingService = inject(OnboardingService);

  productForm: FormGroup;
  attributesFormArray: FormArray;
  variantForm: FormGroup;
  
  currentPhase = 1;
  currentAttributes: ProductAttribute[] = [];
  addedVariants: ProductVariant[] = [];
  addedProducts: ProductSummary[] = [];
  addedNewProduct: boolean = true;
  showActionsButtons: boolean = true;
  // ✅ AJOUT: Gestion des images
  selectedImages: { file: File; preview: string }[] = [];
  
  isAddingVariant = false;
  isAddingProduct = false;
  duplicateVariantError = false;
  productCreationError = ''; // ✅ AJOUT: Gestion des erreurs

  constructor(private fb: FormBuilder) {
    // ✅ CORRECTION: Ajout du champ price avec validation
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      category: ['', Validators.required],
      description: [''],
      price: ['', [Validators.required, Validators.min(0.01)]] // ✅ Prix requis et > 0
    });

    this.attributesFormArray = this.fb.array([this.createAttributeFormGroup()]);
    this.variantForm = this.fb.group({});
  }

  // ✅ AJOUT: Gestion des images
  onImagesSelected(event: any): void {
    const files: FileList = event.target.files;
    if (files && files.length > 0) {
      Array.from(files).forEach(file => {
        if (file.type.startsWith('image/')) {
          const reader = new FileReader();
          reader.onload = (e: any) => {
            this.selectedImages.push({
              file: file,
              preview: e.target.result
            });
          };
          reader.readAsDataURL(file);
        }
      });
    }
  }

  removeImage(index: number): void {
    this.selectedImages.splice(index, 1);
  }

  // MÉTHODES POUR CORRIGER L'ERREUR TYPESCRIPT
  getAttributeFormGroups(): FormGroup[] {
    return this.attributesFormArray.controls as FormGroup[];
  }

  getValuesFormGroups(attributeIndex: number): FormGroup[] {
    const valuesArray = this.getValuesFormArray(attributeIndex);
    return valuesArray.controls as FormGroup[];
  }

  // Nouvelle méthode pour créer le nom du contrôle
  getControlName(attributeName: string): string {
    return attributeName.toLowerCase().replace(/\s+/g, '');
  }

  // Nouvelle méthode pour vérifier si un champ est invalide
  isFieldInvalid(controlName: string): boolean {
    const control = this.variantForm.get(controlName);
    return control ? control.invalid && control.touched : false;
  }

  createAttributeFormGroup(): FormGroup {
    return this.fb.group({
      name: ['', Validators.required],
      values: this.fb.array([this.createValueFormGroup()])
    });
  }

  createValueFormGroup(): FormGroup {
    return this.fb.group({
      value: ['', Validators.required]
    });
  }

  getValuesFormArray(attributeIndex: number): FormArray {
    return this.attributesFormArray.at(attributeIndex).get('values') as FormArray;
  }

  addAttribute(): void {
    this.attributesFormArray.push(this.createAttributeFormGroup());
  }

  removeAttribute(index: number): void {
    if (this.attributesFormArray.length > 1) {
      this.attributesFormArray.removeAt(index);
    }
  }

  addValue(attributeIndex: number): void {
    const valuesArray = this.getValuesFormArray(attributeIndex);
    valuesArray.push(this.createValueFormGroup());
  }

  removeValue(attributeIndex: number, valueIndex: number): void {
    const valuesArray = this.getValuesFormArray(attributeIndex);
    if (valuesArray.length > 1) {
      valuesArray.removeAt(valueIndex);
    }
  }

  areAttributesValid(): boolean {
    return this.attributesFormArray.valid && this.attributesFormArray.length > 0;
  }

  // ✅ CORRECTION: Vérifier aussi le champ price
  goToAttributesPhase(): void {
    if (this.productForm.get('name')?.valid && 
        this.productForm.get('category')?.valid && 
        this.productForm.get('price')?.valid) {
      this.currentPhase = 2;
    }
  }

  addNewProduct(): void {
    this.addedNewProduct = true;
    this.showActionsButtons = false;
  }

  goToVariantCreation(): void {
    if (!this.areAttributesValid()) return;

    // Extraire les attributs du formulaire
    this.currentAttributes = [];
    this.attributesFormArray.controls.forEach((attrGroup) => {
      const name = attrGroup.get('name')?.value;
      const valuesArray = attrGroup.get('values') as FormArray;
      const values = valuesArray.controls.map(v => v.get('value')?.value).filter(v => v);

      if (name && values.length > 0) {
        this.currentAttributes.push({ name, values });
      }
    });

    // Créer le formulaire de variante avec les attributs dynamiques
    this.createVariantForm();
    this.currentPhase = 3;
  }

  createVariantForm(): void {
    const formControls: { [key: string]: any } = {};
    
    // Ajouter un contrôle pour chaque attribut
    this.currentAttributes.forEach(attr => {
      const controlName = this.getControlName(attr.name);
      formControls[controlName] = ['', Validators.required];
    });

    // ✅ CORRECTION: Seulement le stock, plus de prix ici
    formControls['stock'] = ['', [Validators.required, Validators.min(0)]];

    this.variantForm = this.fb.group(formControls);
  }

  addVariant(): void {
    if (this.variantForm.invalid) return;

    this.isAddingVariant = true;
    this.duplicateVariantError = false;

    try {
      // Construire les attributs de la variante
      const attributes: { [key: string]: string } = {};
      this.currentAttributes.forEach(attr => {
        const controlName = this.getControlName(attr.name);
        attributes[attr.name] = this.variantForm.get(controlName)?.value;
      });

      // Vérifier les doublons
      const isDuplicate = this.addedVariants.some(variant => 
        this.currentAttributes.every(attr => 
          variant.attributes[attr.name] === attributes[attr.name]
        )
      );

      if (isDuplicate) {
        this.duplicateVariantError = true;
        return;
      }

      // ✅ CORRECTION: Créer la nouvelle variante SANS prix
      const newVariant: ProductVariant = {
        attributes,
        stock: parseInt(this.variantForm.get('stock')?.value)
        // ✅ Plus de prix dans les variantes
      };

      this.addedVariants.push(newVariant);

      // Réinitialiser le formulaire
      this.variantForm.reset();

      // Petit délai pour l'animation
      setTimeout(() => {
        this.isAddingVariant = false;
      }, 500);

    } catch (error) {
      console.error('Erreur lors de l\'ajout de la variante:', error);
    } finally {
      setTimeout(() => {
        this.isAddingVariant = false;
      }, 500);
    }
  }

  removeVariant(index: number): void {
    this.addedVariants.splice(index, 1);
    this.duplicateVariantError = false;
  }

  AnnulerAddedNewProduct(): void {
    this.addedNewProduct = false;
    this.showActionsButtons = true;
  }

  // ✅ CORRECTION: Méthode addProduct complètement corrigée
  async addProduct(): Promise<void> {
    if (this.addedVariants.length === 0 || !this.productForm.valid) {
      console.error('❌ Formulaire invalide ou pas de variantes');
      return;
    }
    this.isAddingProduct = true;
    this.productCreationError = '';

    try {
      // ✅ CORRECTION: Récupérer et valider toutes les données
      const productName = this.productForm.get('name')?.value;
      const productCategory = this.productForm.get('category')?.value;
      const productDescription = this.productForm.get('description')?.value || '';
      const productPrice = this.productForm.get('price')?.value;

      // Validation des données essentielles
      if (!productName || !productCategory || !productPrice) {
        throw new Error('Le nom, la catégorie et le prix du produit sont requis');
      }

      if (!this.shopId) {
        throw new Error('ID de boutique manquant');
      }

      // ✅ CORRECTION: Parser et valider le prix
      const parsedPrice = parseFloat(productPrice);
      if (isNaN(parsedPrice) || parsedPrice <= 0) {
        throw new Error('Le prix doit être un nombre valide supérieur à 0');
      }

      // ✅ CORRECTION: Préparer les données du produit avec validation
      const productData: CreateProductRequestDTO = {
        shopId: this.shopId,
        name: productName,
        category: productCategory,
        description: productDescription,
        attributes: this.currentAttributes,
        variants: this.addedVariants, // Variantes sans prix
        price: parsedPrice, // ✅ Prix unique au niveau produit
        images: this.selectedImages.map(img => img.file)
      }; 

      console.log('🚀 Données du produit à envoyer:', productData);

      // ✅ CORRECTION: Utiliser le service OnboardingService
      this.onboardingService.addProduct(productData).subscribe({
        next: (response) => {
          console.log('✅ Produit ajouté avec succès:', response);

          // ✅ CORRECTION: Créer ProductSummary avec le prix du produit
          const productSummary: ProductSummary = {
            id: response.id || Date.now().toString(),
            name: productData.name,
            category: productData.category,
            variantCount: this.addedVariants.length,
            totalStock: this.addedVariants.reduce((sum, v) => sum + v.stock, 0),
            price: parsedPrice // ✅ Prix unique du produit
          };

          this.addedProducts.push(productSummary);
          this.productAdded.emit(productSummary);
          
          // Réinitialiser pour un nouveau produit
          this.resetForNewProduct();
          this.addedNewProduct = false;
          this.showActionsButtons = true;

        },
        error: (error) => {
          console.error('❌ Erreur lors de l\'ajout du produit:', error);
          this.productCreationError = error.message || 'Une erreur est survenue lors de l\'ajout du produit.';
        },
        complete: () => {
          this.isAddingProduct = false;
        }
      });

    } catch (error: any) {
      console.error('❌ Erreur lors de la préparation du produit:', error);
      this.productCreationError = error.message || 'Une erreur est survenue lors de la préparation du produit.';
      this.isAddingProduct = false;
    }
  }

  resetForNewProduct(): void {
    this.productForm.reset();
    this.attributesFormArray.clear();
    this.attributesFormArray.push(this.createAttributeFormGroup());
    this.variantForm = this.fb.group({});
    this.currentAttributes = [];
    this.addedVariants = [];
    this.selectedImages = []; // ✅ AJOUT: Reset des images
    this.currentPhase = 1;
    this.duplicateVariantError = false;
    this.productCreationError = '';
  }

  backToAttributes(): void {
    this.currentPhase = 2;
    this.addedVariants = [];
    this.duplicateVariantError = false;
    this.productCreationError = '';
  }

  completeOnboarding(): void {
    this.onboardingComplete.emit();
  }
}