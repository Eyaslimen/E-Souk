import { Component, ChangeDetectionStrategy, signal, OnInit, computed } from '@angular/core';
import { NgIf } from '@angular/common';
import { UserProfile } from '../../interfaces/user-info';
import { UserInfoService } from '../../services/user-info.service';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/AuthService';

@Component({
  selector: 'app-navbar',
  standalone:true,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgIf,RouterLink]
})
export class NavbarComponent implements OnInit {

  isMobileMenuOpen = signal(false);
  // Utilisez un signal pour userInfo
  userInfo = signal<UserProfile | null>(null);
  test:boolean=true;
  // Propriétés computées pour améliorer les performances
  isLoggedIn = computed(() => this.userInfo() !== null);
  isClient = computed(() => this.userInfo()?.role === 'CLIENT');
  isVendor = computed(() => this.userInfo()?.role === 'VENDOR');
  isAdmin = computed(() => this.userInfo()?.role === 'ADMIN');

  constructor(private userInfoService: UserInfoService,
    private authService: AuthService,
    private router : Router
  ) {}

 ngOnInit(): void {
    this.userInfoService.getUserProfile().subscribe({
      next: (userInfo: UserProfile | null) => {
        this.userInfo.set(userInfo);
        this.test=false; // Marquer le chargement comme terminé
        console.log("Données utilisateur reçues:", userInfo);
      },
      error: (error) => {
        console.error('Erreur lors du chargement du profil:', error);
        this.test=false; // Même en cas d'erreur
      }
    });
  }
  toggleMobileMenu() {
    this.isMobileMenuOpen.update(open => !open);
  }
  navigateToCartTab() {
  this.router.navigate(['/profile'], { 
    queryParams: { tab: 'panier' } 
  });
  }
    navigateToProfile() {
  this.router.navigate(['/profile'], { 
    queryParams: { tab: 'personnel' } 
  });
  }
}