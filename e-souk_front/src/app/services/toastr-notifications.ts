import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

export interface NotificationConfig {
  title?: string;
  message: string;
  duration?: number;
  showCloseButton?: boolean;
  showProgressBar?: boolean;
  enableHtml?: boolean;
  position?: 'top-right' | 'top-left' | 'top-center' | 'bottom-right' | 'bottom-left' | 'bottom-center';
}


@Injectable({
  providedIn: 'root'
})
export class ToastrNotifications {
    
  private defaultConfig: Partial<NotificationConfig> = {
    duration: 4000,
    showCloseButton: true,
    showProgressBar: true,
    enableHtml: true,
    position: 'top-right'
  };

  constructor(private toastr: ToastrService) {}

  // 🎉 Notifications de succès
  success(message: string, title?: string, config?: Partial<NotificationConfig>) {
    const finalConfig = { ...this.defaultConfig, ...config };
    
    return this.toastr.success(
      message, 
      title || ' Succès',
      {
        timeOut: finalConfig.duration,
        closeButton: finalConfig.showCloseButton,
        progressBar: finalConfig.showProgressBar,
        enableHtml: finalConfig.enableHtml,
        positionClass: `toast-${finalConfig.position}`
      }
    );
  }

  // ❌ Notifications d'erreur
  error(message: string, title?: string, config?: Partial<NotificationConfig>) {
    const finalConfig = { ...this.defaultConfig, duration: 6000, ...config }; // Plus long pour les erreurs
    
    return this.toastr.error(
      message,
      title || '❌ Erreur',
      {
        timeOut: finalConfig.duration,
        closeButton: finalConfig.showCloseButton,
        progressBar: finalConfig.showProgressBar,
        enableHtml: finalConfig.enableHtml,
        positionClass: `toast-${finalConfig.position}`
      }
    );
  }

  // ⚠️ Notifications d'avertissement
  warning(message: string, title?: string, config?: Partial<NotificationConfig>) {
    const finalConfig = { ...this.defaultConfig, duration: 5000, ...config };
    
    return this.toastr.warning(
      message,
      title || '⚠️ Attention',
      {
        timeOut: finalConfig.duration,
        closeButton: finalConfig.showCloseButton,
        progressBar: finalConfig.showProgressBar,
        enableHtml: finalConfig.enableHtml,
        positionClass: `toast-${finalConfig.position}`
      }
    );
  }

  // 💡 Notifications d'information
  info(message: string, title?: string, config?: Partial<NotificationConfig>) {
    const finalConfig = { ...this.defaultConfig, ...config };
    
    return this.toastr.info(
      message,
      title || '💡 Information',
      {
        timeOut: finalConfig.duration,
        closeButton: finalConfig.showCloseButton,
        progressBar: finalConfig.showProgressBar,
        enableHtml: finalConfig.enableHtml,
        positionClass: `toast-${finalConfig.position}`
      }
    );
  }

  // 🔄 Notification de chargement (ne se ferme pas automatiquement)
  loading(message: string, title?: string) {
    return this.toastr.info(
      message,
      title || '⏳ Chargement...',
      {
        timeOut: 0, // Ne se ferme pas automatiquement
        closeButton: false,
        progressBar: false,
        enableHtml: true,
        positionClass: 'toast-top-right'
      }
    );
  }

  // 🗑️ Effacer tous les toasts
  clear() {
    this.toastr.clear();
  }

  // 🗑️ Effacer un toast spécifique
  remove(toastId: number) {
    this.toastr.remove(toastId);
  }

  // 🚀 Méthodes spécialisées pour des cas d'usage courants

  // Succès de formulaire
  formSuccess(action: string = 'action') {
    return this.success(
      `Votre ${action} a été effectué avec succès ! 🎉`,
      '✅ Opération réussie'
    );
  }

  // Erreur de formulaire
  formError(error?: any) {
    const message = error?.error?.message || error?.message || 'Une erreur inattendue s\'est produite';
    return this.error(
      `${message} 😞<br><small>Veuillez réessayer ou contactez le support si le problème persiste.</small>`,
      '❌ Erreur de formulaire',
      { enableHtml: true }
    );
  }

  // Validation de formulaire
  formValidation(message: string = 'Veuillez remplir correctement tous les champs obligatoires') {
    return this.warning(
      `${message} 📝`,
      '⚠️ Formulaire incomplet'
    );
  }

  // Connexion réussie
  loginSuccess(username?: string) {
    const name = username ? ` ${username}` : '';
    return this.success(
      `Bienvenue${name} ! Vous êtes maintenant connecté 👋`,
      '🎊 Connexion réussie'
    );
  }

  // Déconnexion
  logoutSuccess() {
    return this.info(
      'Vous avez été déconnecté avec succès. À bientôt ! 👋',
      '👋 Déconnexion'
    );
  }

  // Fichier uploadé avec succès
  fileUploadSuccess(filename: string) {
    return this.success(
      `Le fichier "${filename}" uploadé 📎`);
  }

  // Erreur de fichier
  fileError(message: string) {
    return this.error(
      `${message} 📁`,
      '❌ Erreur de fichier'
    );
  }

  // Confirmation d'action
  confirmationMessage(action: string) {
    return this.info(
      `${action} confirmé avec succès ✓`,
      '✅ Confirmation'
    );
  }

  // Réseau/Serveur indisponible
  networkError() {
    return this.error(
      'Impossible de se connecter au serveur. Vérifiez votre connexion internet 🌐',
      '🔌 Problème de connexion',
      { duration: 8000 }
    );
  }

  // Session expirée
  sessionExpired() {
    return this.warning(
      'Votre session a expiré. Veuillez vous reconnecter 🔐',
      '⏰ Session expirée',
      { duration: 0 } // Ne se ferme pas automatiquement
    );
  }

  // Sauvegarde automatique
  autoSave() {
    return this.info(
      'Vos données ont été sauvegardées automatiquement 💾',
      '🔄 Sauvegarde auto',
      { duration: 2000, showProgressBar: false }
    );
  }
}
