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
];