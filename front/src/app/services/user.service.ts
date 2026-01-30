import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../interfaces/user.interface';
import { AuthResponse } from '../features/auth/interfaces/auth-response.interface';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) {}

    // Récupérer le profil de l'utilisateur
    getProfile(): Observable<User> {
        return this.http.get<User>(`${this.baseUrl}/auth/me`);
    }

    // Mettre à jour le profil de l'utilisateur
    updateProfile(data: { username?: string; email?: string; password?: string }): Observable<AuthResponse> {
        return this.http.put<AuthResponse>(`${this.baseUrl}/auth/me`, data);
    }
}