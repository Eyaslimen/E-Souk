import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZoneChangeDetection, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';
import { authInterceptor } from './services/auth-interceptor.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor]) // Utilisation de la nouvelle syntaxe
    ),
    provideAnimations(),
    importProvidersFrom(FormsModule, ReactiveFormsModule), // required animations providers
    provideToastr({
      timeOut: 4000,
      positionClass: 'toast-top-right', // Position en haut à droite
      preventDuplicates: true,
      enableHtml: true,
      closeButton: true, // Bouton de fermeture stylé
      progressBar: true, // Barre de progression
      progressAnimation: 'increasing',
      newestOnTop: true,
      tapToDismiss: true,
      // Styles personnalisés
      toastClass: 'ngx-toastr custom-toast',
      titleClass: 'toast-title',
      messageClass: 'toast-message',
      // Animation plus fluide
      easeTime: 300,
      extendedTimeOut: 1000
    }) // Toastr providers
  ]
};

