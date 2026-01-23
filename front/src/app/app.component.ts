import { Component } from '@angular/core';
import { SessionService } from './services/session.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'front';
  showMenu = false;

  private noMenuRoutes = ['/', '/login', '/register'];

  constructor(
    private router: Router,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.updateMenu(event.url);
      });

    this.updateMenu(this.router.url);
  }

  // Afficher ou non le menu selon la page actuelle
  private updateMenu(url: string): void {
    this.showMenu = !this.noMenuRoutes.includes(url) && this.sessionService.isLoggedIn();
  }

  // Déconnexion de l'utilisateur
  logout(): void {
    this.sessionService.logOut();
    this.router.navigate(['/']);
  }
}
