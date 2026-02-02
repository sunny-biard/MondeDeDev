import { Component, HostListener, OnInit } from '@angular/core';
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
  showNavLinks = false;
  isMobile = false;
  isMobileSidebarOpen = false;

  constructor(
    private router: Router,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    // Vérifier si on doit afficher le menu et les liens de navigation au chargement initial
    this.checkIfMobile();
    this.checkMenuVisibility();

    // Écouter les changements de route
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.checkMenuVisibility();
      this.closeMobileSidebar(); // Fermer la sidebar lors du changement de route
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any): void {
    this.checkIfMobile();
  }

  private checkIfMobile(): void {
    this.isMobile = window.innerWidth <= 768;
    this.checkMenuVisibility();
  }

  private checkMenuVisibility(): void {
    const currentUrl = this.router.url;
    const isAuthPage = currentUrl === '/login' || currentUrl === '/register';
    
    // Sur mobile, masquer le menu sur les pages login et register
    // Sur ordinateur, masquer le menu sur la page d'accueil
    if (this.isMobile && isAuthPage) {
      this.showMenu = false;
    } else {
      this.showMenu = currentUrl !== '/';
    }
    
    // Ne pas afficher les liens de navigation sur les pages login et register
    this.showNavLinks = !isAuthPage;
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