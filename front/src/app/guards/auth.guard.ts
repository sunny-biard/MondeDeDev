import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { SessionService } from '../services/session.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  
  constructor(
    private sessionService: SessionService,
    private router: Router
  ) {}

  // Vérifie si l'utilisateur peut accéder à la route
  canActivate(): boolean {
    if (this.sessionService.isLoggedIn()) {
      return true;
    }

    // Si non connecté, rediriger vers la page de connexion
    this.router.navigate(['/login']);
    return false;
  }
}
