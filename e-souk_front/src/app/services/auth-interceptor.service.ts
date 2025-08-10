import { inject, Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpInterceptorFn } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './AuthService';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  console.log('AuthInterceptor initialized');
  // Récupérer le token depuis le service d'authentification
  const token = authService.getToken();
  console.log('Interceptor token:', token);

  // Si le token existe, l'ajouter aux en-têtes de la requête
  if (token) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    console.log('Request with token:', authReq);
    return next(authReq);
  }

  // Sinon, laisser passer la requête sans modification
  console.log('Request without token:', req);
  return next(req);
};