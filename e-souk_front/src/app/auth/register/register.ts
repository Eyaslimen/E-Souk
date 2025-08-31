// 📁 src/app/auth/register/register.ts - VERSION CORRIGÉE
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common'; // Retirez NgIf, utilisez CommonModule seulement
import { AuthService } from '../../services/AuthService';
import { RegisterRequestDTO } from '../../interfaces/auth';
import { ToastrNotifications } from '../../services/toastr-notifications';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, CommonModule,RouterLink], // Retirez NgIf
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  registerForm: FormGroup;
  isLoading = false;
  selectedFile: File | null = null;
  selectedFileName = '';
  
  // ✅ AJOUT DES PROPRIÉTÉS MANQUANTES utilisées dans le template
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private notification: ToastrNotifications
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9+\-\s]+$/)]],
      address: ['', [Validators.required, Validators.minLength(5)]],
      codePostal: ['', [Validators.required, Validators.pattern(/^\d{5}$/)]],
      role: ['', [Validators.required]]
    });
  }

  // 🎨 Méthode de test pour voir tous les types de notifications
  testAllNotifications() {
    // Test des notifications de base
    this.notification.success('Opération réussie !');
    
    setTimeout(() => this.notification.info('Information importante'), 1000);
    setTimeout(() => this.notification.warning('Attention à ce point'), 2000);
    setTimeout(() => this.notification.error('Une erreur est survenue'), 3000);
    
    // Test des notifications spécialisées
    setTimeout(() => this.notification.formSuccess('inscription'), 4000);
    setTimeout(() => this.notification.loginSuccess('John'), 5000);
    setTimeout(() => this.notification.autoSave(), 6000);
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return field ? field.invalid && field.touched : false;
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        // Utiliser à la fois le service de notification ET les messages locaux
        this.errorMessage = 'La taille du fichier ne doit pas dépasser 5MB';
        this.notification.fileError('La taille du fichier ne doit pas dépasser 5MB');
        return;
      }
      
      if (!file.type.startsWith('image/')) {
        this.errorMessage = 'Veuillez sélectionner un fichier image (JPG, PNG, etc.)';
        this.notification.fileError('Veuillez sélectionner un fichier image (JPG, PNG, etc.)');
        return;
      }
      
      this.selectedFile = file;
      this.selectedFileName = file.name;
      this.errorMessage = ''; // Reset l'erreur
      this.notification.fileUploadSuccess(file.name);
    }
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      // Notification de chargement
      const loadingToast = this.notification.loading('Inscription en cours... Veuillez patienter');
      
      const registerData: RegisterRequestDTO = {
        ...this.registerForm.value,
        profilePicture: this.selectedFile || undefined
      };
      
      this.authService.register(registerData).subscribe({
        next: (response) => {
          // Effacer le toast de chargement
          this.notification.clear();
          
          // Notification de succès spécialisée
          this.notification.formSuccess('inscription');
          
          // Message local de succès
          this.successMessage = 'Inscription réussie ! Redirection vers la connexion...';
          
          // Message personnalisé avec redirection
          setTimeout(() => {
            this.notification.info(
              'Vous allez être redirigé vers la page de connexion...',
              '🔄 Redirection',
              { duration: 2000 }
            );
          }, 1500);
          
          this.isLoading = false;
          
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        },
        error: (error) => {
          // Effacer le toast de chargement
          this.notification.clear();
          
          // Notification d'erreur spécialisée
          this.notification.formError(error);
          
          // Message local d'erreur
          this.errorMessage = error.error?.message || 'Erreur lors de l\'inscription';
          
          this.isLoading = false;
        }
      });
    } else {
      // Notification de validation spécialisée
      this.notification.formValidation();
      
      // Message local d'erreur
      this.errorMessage = 'Veuillez remplir correctement tous les champs obligatoires';
      
      Object.keys(this.registerForm.controls).forEach(key => {
        this.registerForm.get(key)?.markAsTouched();
      });
    }
  }
}