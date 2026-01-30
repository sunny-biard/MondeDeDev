import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserProfile } from '../interfaces/user-profile.interface';
import { AuthResponse } from '../features/auth/interfaces/auth-response.interface';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = environment.baseUrl;

  constructor(private http: HttpClient) {}

  // Mettre à jour le profil de l'utilisateur
  updateProfile(data: { username?: string; email?: string; password?: string }): Observable<AuthResponse> {
    return this.http.put<AuthResponse>(`${this.baseUrl}/auth/me`, data);
  }

  // Récupérer le profil de l'utilisateur
  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.baseUrl}/auth/me`);
  }
}