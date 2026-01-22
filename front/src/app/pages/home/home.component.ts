import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  constructor(private router: Router) {}

  ngOnInit(): void {
  }

  // Redirige vers la page de connexion
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  // Redirige vers la page d'inscription
  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
