import { Component, HostListener, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter, pipe, Subject, takeUntil } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  showMenu = false;
  showNavLinks = false;
  isMobile = false;
  isMobileSidebarOpen = false;
  private destroy$ = new Subject<void>();

  constructor(
    private router: Router,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    // Vérifier si on doit afficher le menu et les liens de navigation au chargement initial
    this.checkIfMobile();
    this.checkMenuVisibility();

    // Suivre les changements de route
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      pipe(takeUntil(this.destroy$))
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
    // Sur desktop, masquer le menu sur la page d'accueil
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
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
