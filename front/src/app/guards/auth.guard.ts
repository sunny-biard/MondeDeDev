import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // Vérifie si l'utilisateur peut accéder à la route
  canActivate(): boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    // Si non connecté, rediriger vers la page de connexion
    this.router.navigate(['/login']);
    return false;
  }
}
