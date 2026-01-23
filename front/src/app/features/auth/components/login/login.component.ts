import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SessionService } from '../../../../services/session.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  onError: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private sessionService: SessionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      identifier: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          this.sessionService.logIn(response.token);
          this.authService.me().subscribe({
            next: (user) => {
              this.sessionService.setUser(user);
              this.router.navigate(['/posts']);
            },
            error: () => {
              this.onError = true;
            }
          });
       },
        error: () => {
          this.onError = true;
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
