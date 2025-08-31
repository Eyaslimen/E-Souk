import { Routes } from '@angular/router';
import { Header } from './features/shopDetailsPage/header/header';

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
    path: 'products',
    loadComponent: () => import('./features/products/products/products').then(m => m.Products),
  },
    {
    path: 'shops/:name',
    loadComponent: () => import('./features/shopDetailsPage/shop-page/shop-page').then(m => m.ShopPage),
  },
    {
    path: 'shops',
    loadComponent: () => import('./features/shops/shops/shops').then(m => m.Shops),
  },
    {
    path: 'onboarding',
    loadComponent: () => import('./features/shopCreation/onboarding/onboarding').then(m => m.OnboardingComponent),
  },
   {
    path: 'products/:id',
    loadComponent: () => import('./features/products/product-details/product-details').then(m => m.ProductDetailsComponent),
  },

];