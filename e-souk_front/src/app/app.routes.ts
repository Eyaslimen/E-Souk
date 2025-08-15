import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./landing/landing-page.component').then(m => m.LandingPageComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('./auth/register/register').then(m => m.Register),
  },
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login').then(m => m.Login),
  },
  {
    path: 'user',
    loadComponent: () => import('./features/users/user/user').then(m => m.User),
  },
  {
    path: 'profile',
    loadComponent: () => import('./features/users/profile/profile').then(m => m.Profile),
  },
    {
    path: 'onboarding',
    loadComponent: () => import('./features/shops/onboarding/onboarding').then(m => m.OnboardingComponent),
  },
];