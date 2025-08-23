import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthResponseDTO, LoginRequestDTO } from '../interfaces/auth';
import { UserProfile } from '../interfaces/UserProfile';
import { AuthService } from './AuthService';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private token: string | null = null;
  private readonly Auth_API_URL = 'http://localhost:8080/api/auth';
  constructor(private http: HttpClient, private authService: AuthService) {}

  getProfile(): Observable<UserProfile> {
    this.token = this.authService.getToken();
        console.log('Token:', this.token);
    return this.http.get<UserProfile>(`${this.Auth_API_URL}/profile`);
  }


}