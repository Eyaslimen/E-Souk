import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthResponseDTO, LoginRequestDTO } from '../interfaces/auth';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'jwt_token';
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequestDTO): Observable<AuthResponseDTO> {
    return this.http.post<AuthResponseDTO>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap(response => {
          if (response.token) {
            localStorage.setItem(this.TOKEN_KEY, response.token);
            this.isAuthenticatedSubject.next(true);
          }
        })
      );
  }

 
  register(userData: any): Observable<AuthResponseDTO> {
    console.log('Registering user:', userData);
    const formData = new FormData();
    formData.append('username', userData.username);
    formData.append('email', userData.email);
    formData.append('password', userData.password);
    formData.append('phone', userData.phone);
    formData.append('address', userData.address);
    formData.append('codePostal', userData.codePostal);
    formData.append('role', userData.role);

    // Ajout du fichier seulement s'il est défini
    if (userData.profilePicture) {
      formData.append('profilePicture', userData.profilePicture);
    }

    return this.http.post<AuthResponseDTO>(`${this.API_URL}/register`, formData);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.isAuthenticatedSubject.next(false);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    // Vérifier si le token n'est pas expiré
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }
}