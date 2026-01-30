import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { SessionService } from './services/session.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'MDD';
  showMenu = false;
  isMobileSidebarOpen = false;

  constructor(
    private router: Router,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    // Vérifier si on doit afficher le menu
    this.checkMenuVisibility();

    // Écouter les changements de route
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.checkMenuVisibility();
      this.closeMobileSidebar(); // Fermer la sidebar lors du changement de route
    });
  }

  private checkMenuVisibility(): void {
    const currentUrl = this.router.url;
    // Ne pas afficher le menu sur les pages de login et register
    this.showMenu = !currentUrl.includes('/login') && !currentUrl.includes('/register') && currentUrl !== '/';
  }

  toggleMobileSidebar(): void {
    this.isMobileSidebarOpen = !this.isMobileSidebarOpen;
    
    // Empêcher le scroll du body quand la sidebar est ouverte
    if (this.isMobileSidebarOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
  }

  closeMobileSidebar(): void {
    this.isMobileSidebarOpen = false;
    document.body.style.overflow = '';
  }

  logout(): void {
    this.sessionService.logOut();
    this.router.navigate(['/login']);
  }
}