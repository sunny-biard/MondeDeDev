import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserProfile } from '../interfaces/user-profile.interface';

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private readonly TOKEN_KEY = 'auth_token';
  private userSubject = new BehaviorSubject<UserProfile | null>(null);
  public user$: Observable<UserProfile | null> = this.userSubject.asObservable();

  constructor() {
    if (this.getToken()) {}
  }

  // Vérifie si l'utilisateur est connecté
  public isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // Récupère le token JWT
  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Enregistre le token et initialise la session
  public logIn(token: string, user?: UserProfile): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    if (user) {
      this.userSubject.next(user);
    }
  }

  // Définit les informations utilisateur
  public setUser(user: UserProfile): void {
    this.userSubject.next(user);
  }

  // Récupère les informations utilisateur actuelles
  public getUser(): UserProfile | null {
    return this.userSubject.value;
  }

  // Déconnexion
  public logOut(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.userSubject.next(null);
  }
}