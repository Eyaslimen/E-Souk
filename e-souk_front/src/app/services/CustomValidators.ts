// src/app/utils/validators.ts
import { AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

export class CustomValidators {
  
  // Validateur pour les noms de marque
  static brandName(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const value = control.value.toString().trim();
    
    // Vérifier la longueur
    if (value.length < 2 || value.length > 100) {
      return { brandNameLength: { min: 2, max: 100, actual: value.length } };
    }
    
    // Vérifier les caractères interdits
    const forbiddenChars = /[<>\"'&]/;
    if (forbiddenChars.test(value)) {
      return { brandNameForbiddenChars: true };
    }
    
    // Vérifier qu'il ne commence/termine pas par des espaces
    if (value !== value.trim()) {
      return { brandNameWhitespace: true };
    }
    
    return null;
  }

  // Validateur pour les fichiers image
  static imageFile(control: AbstractControl): ValidationErrors | null {
    const file = control.value as File;
    if (!file) return null;
    
    const validTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/webp'];
    const maxSize = 5 * 1024 * 1024; // 5MB
    
    if (!validTypes.includes(file.type)) {
      return { invalidFileType: { allowedTypes: validTypes, actual: file.type } };
    }
    
    if (file.size > maxSize) {
      return { fileTooLarge: { maxSize: maxSize, actual: file.size } };
    }
    
    return null;
  }

  // Validateur pour les prix
  static price(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    
    const value = parseFloat(control.value);
    
    if (isNaN(value)) {
      return { invalidPrice: true };
    }
    
    if (value < 0) {
      return { priceNegative: true };
    }
    
    if (value > 10000) {
      return { priceTooHigh: { max: 10000 } };
    }
    
    // Vérifier qu'il n'y a pas plus de 2 décimales
    const decimals = (value.toString().split('.')[1] || '').length;
    if (decimals > 2) {
      return { tooManyDecimals: { max: 2 } };
    }
    
    return null;
  }

  // Validateur pour les stocks
  static stock(control: AbstractControl): ValidationErrors | null {
    if (control.value === null || control.value === undefined) return null;
    
    const value = parseInt(control.value);
    
    if (isNaN(value)) {
      return { invalidStock: true };
    }
    
    if (value < 0) {
      return { stockNegative: true };
    }
    
    if (value > 999999) {
      return { stockTooHigh: { max: 999999 } };
    }
    
    return null;
  }

  // Validateur pour les frais de livraison
  static deliveryFee(control: AbstractControl): ValidationErrors | null {
    if (!control.value && control.value !== 0) return null;
    
    const value = parseFloat(control.value);
    
    if (isNaN(value)) {
      return { invalidDeliveryFee: true };
    }
    
    if (value < 0) {
      return { deliveryFeeNegative: true };
    }
    
    if (value > 100) {
      return { deliveryFeeTooHigh: { max: 100 } };
    }
    
    return null;
  }

  // Validateur pour les attributs uniques
  static uniqueAttributeNames(control: AbstractControl): ValidationErrors | null {
    if (!control.value || !Array.isArray(control.value)) return null;
    
    const names = control.value
      .map((attr: any) => attr.name?.toLowerCase().trim())
      .filter((name: string) => name);
    
    const uniqueNames = new Set(names);
    
    if (names.length !== uniqueNames.size) {
      return { duplicateAttributeNames: true };
    }
    
    return null;
  }

  // Validateur pour les valeurs d'attributs uniques
  static uniqueAttributeValues(control: AbstractControl): ValidationErrors | null {
    if (!control.value || !Array.isArray(control.value)) return null;
    
    const values = control.value
      .map((val: any) => val.value?.toLowerCase().trim())
      .filter((value: string) => value);
    
    const uniqueValues = new Set(values);
    
    if (values.length !== uniqueValues.size) {
      return { duplicateAttributeValues: true };
    }
    
    return null;
  }
}

// src/app/utils/file.utils.ts
export class FileUtils {
  
  static formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  static isValidImageType(file: File): boolean {
    const validTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/webp', 'image/gif'];
    return validTypes.includes(file.type);
  }

  static isFileSizeValid(file: File, maxSizeInMB: number = 5): boolean {
    const maxSizeInBytes = maxSizeInMB * 1024 * 1024;
    return file.size <= maxSizeInBytes;
  }

  static createImagePreview(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      if (!this.isValidImageType(file)) {
        reject(new Error('Type de fichier invalide'));
        return;
      }

      const reader = new FileReader();
      reader.onload = (e) => {
        resolve(e.target?.result as string);
      };
      reader.onerror = () => {
        reject(new Error('Erreur lors de la lecture du fichier'));
      };
      reader.readAsDataURL(file);
    });
  }

  static compressImage(file: File, maxWidth: number = 800, quality: number = 0.8): Promise<File> {
    return new Promise((resolve, reject) => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      const img = new Image();

      img.onload = () => {
        // Calculer les nouvelles dimensions
        const ratio = Math.min(maxWidth / img.width, maxWidth / img.height);
        canvas.width = img.width * ratio;
        canvas.height = img.height * ratio;

        // Dessiner l'image redimensionnée
        ctx?.drawImage(img, 0, 0, canvas.width, canvas.height);

        // Convertir en blob puis en file
        canvas.toBlob((blob) => {
          if (blob) {
            const compressedFile = new File([blob], file.name, {
              type: file.type,
              lastModified: Date.now()
            });
            resolve(compressedFile);
          } else {
            reject(new Error('Erreur lors de la compression'));
          }
        }, file.type, quality);
      };

      img.onerror = () => {
        reject(new Error('Erreur lors du chargement de l\'image'));
      };

      img.src = URL.createObjectURL(file);
    });
  }
}

// src/app/utils/form.utils.ts
export class FormUtils {
  
  static markFormGroupTouched(formGroup: any): void {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);
      if (control) {
        control.markAsTouched({ onlySelf: true });
        
        // Si c'est un FormArray ou FormGroup, marquer récursivement
        if (control.controls) {
          this.markFormGroupTouched(control);
        }
      }
    });
  }

  static getFormErrors(formGroup: any): { [key: string]: any } {
    const formErrors: { [key: string]: any } = {};

    Object.keys(formGroup.controls).forEach(key => {
      const controlErrors = formGroup.get(key)?.errors;
      if (controlErrors) {
        formErrors[key] = controlErrors;
      }
    });

    return formErrors;
  }

  static scrollToFirstError(formElement: HTMLElement): void {
    const firstErrorField = formElement.querySelector('.form-input.error, .form-select.error, .form-textarea.error') as HTMLElement;
    
    if (firstErrorField) {
      firstErrorField.scrollIntoView({ 
        behavior: 'smooth', 
        block: 'center' 
      });
      firstErrorField.focus();
    }
  }
}

// src/app/utils/animation.utils.ts
export class AnimationUtils {
  
  static fadeIn(element: HTMLElement, duration: number = 300): Promise<void> {
    return new Promise((resolve) => {
      element.style.opacity = '0';
      element.style.transition = `opacity ${duration}ms ease-in-out`;
      
      // Force reflow
      element.offsetHeight;
      
      element.style.opacity = '1';
      
      setTimeout(() => {
        element.style.transition = '';
        resolve();
      }, duration);
    });
  }

  static slideDown(element: HTMLElement, duration: number = 300): Promise<void> {
    return new Promise((resolve) => {
      const height = element.scrollHeight;
      
      element.style.height = '0';
      element.style.overflow = 'hidden';
      element.style.transition = `height ${duration}ms ease-out`;
      
      // Force reflow
      element.offsetHeight;
      
      element.style.height = `${height}px`;
      
      setTimeout(() => {
        element.style.height = '';
        element.style.overflow = '';
        element.style.transition = '';
        resolve();
      }, duration);
    });
  }

  static slideUp(element: HTMLElement, duration: number = 300): Promise<void> {
    return new Promise((resolve) => {
      const height = element.scrollHeight;
      
      element.style.height = `${height}px`;
      element.style.overflow = 'hidden';
      element.style.transition = `height ${duration}ms ease-out`;
      
      // Force reflow
      element.offsetHeight;
      
      element.style.height = '0';
      
      setTimeout(() => {
        element.style.height = '';
        element.style.overflow = '';
        element.style.transition = '';
        resolve();
      }, duration);
    });
  }

  static shake(element: HTMLElement): void {
    element.style.animation = 'shake 0.5s ease-in-out';
    
    setTimeout(() => {
      element.style.animation = '';
    }, 500);
  }
}

// src/app/utils/product.utils.ts
export class ProductUtils {
  
  static generateVariantCombinations(attributes: { name: string; values: string[] }[]): Array<{ [key: string]: string }> {
    if (attributes.length === 0) return [];
    
    const result: Array<{ [key: string]: string }> = [];
    
    function generateCombinations(currentCombination: { [key: string]: string }, remainingAttributes: typeof attributes) {
      if (remainingAttributes.length === 0) {
        result.push({ ...currentCombination });
        return;
      }
      
      const [currentAttribute, ...restAttributes] = remainingAttributes;
      
      currentAttribute.values.forEach(value => {
        generateCombinations(
          { ...currentCombination, [currentAttribute.name]: value },
          restAttributes
        );
      });
    }
    
    generateCombinations({}, attributes);
    return result;
  }

  static calculateProductStats(variants: Array<{ price: number; stock: number }>) {
    if (variants.length === 0) {
      return { minPrice: 0, maxPrice: 0, totalStock: 0, averagePrice: 0 };
    }

    const prices = variants.map(v => v.price);
    const stocks = variants.map(v => v.stock);

    return {
      minPrice: Math.min(...prices),
      maxPrice: Math.max(...prices),
      totalStock: stocks.reduce((sum, stock) => sum + stock, 0),
      averagePrice: prices.reduce((sum, price) => sum + price, 0) / prices.length
    };
  }

  static validateVariantData(variants: Array<{ price: number; stock: number; attributes: any }>): string[] {
    const errors: string[] = [];

    if (variants.length === 0) {
      errors.push('Au moins une variante est requise');
      return errors;
    }

    variants.forEach((variant, index) => {
      if (!variant.price || variant.price <= 0) {
        errors.push(`Le prix de la variante ${index + 1} doit être supérieur à 0`);
      }

      if (variant.stock < 0) {
        errors.push(`Le stock de la variante ${index + 1} ne peut pas être négatif`);
      }

      if (!variant.attributes || Object.keys(variant.attributes).length === 0) {
        errors.push(`La variante ${index + 1} doit avoir au moins un attribut`);
      }
    });

    return errors;
  }

  static formatPrice(price: number, currency: string = '€'): string {
    return `${price.toFixed(2)} ${currency}`;
  }

  static formatStock(stock: number): string {
    if (stock === 0) return 'Rupture de stock';
    if (stock === 1) return '1 article';
    return `${stock} articles`;
  }
}