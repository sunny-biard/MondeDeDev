import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { TopicService } from '../../services/topic.service';
import { SessionService } from '../../services/session.service';
import { Topic } from '../../interfaces/topic.interface';
import { User } from '../../interfaces/user.interface';
import { AuthResponse } from '../../features/auth/interfaces/auth-response.interface';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-me',
  templateUrl: './me.component.html',
  styleUrls: ['./me.component.scss']
})
export class MeComponent implements OnInit {
  profileForm!: FormGroup;
  subscriptions: Topic[] = [];
  private initialFormValues: any;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private topicService: TopicService,
    private sessionService: SessionService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadUserProfile();
    this.loadSubscriptions();
  }

  // Initialise le formulaire
  private initForm(): void {
    this.profileForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.minLength(8), 
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
      ]]
    });
  }

  // Charge le profil de l'utilisateur
  private loadUserProfile(): void {
    this.userService.getProfile()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (user: User) => {
          this.profileForm.patchValue({
            username: user.username,
            email: user.email,
            password: ''
          });
          this.initialFormValues = this.profileForm.value;
          this.sessionService.setUser(user);
        },
        error: (error) => {
          console.error("Erreur lors du chargement du profil:", error);
        }
      });
  }

  // Charge les abonnements de l'utilisateur
  private loadSubscriptions(): void {
    this.topicService.getUserSubscriptions()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (topics: Topic[]) => {
          this.subscriptions = topics;
        },
        error: (error) => {
          console.error("Erreur lors du chargement des abonnements:", error);
        }
      });
  }

  // Vérifie si le formulaire a été modifié
  formValuesChanges(): boolean {
    if (!this.initialFormValues) return false;
    
    const currentValues = this.profileForm.value;
    return (
      currentValues.username !== this.initialFormValues.username ||
      currentValues.email !== this.initialFormValues.email ||
      (currentValues.password && currentValues.password.length > 0)
    );
  }

  // Soumet le formulaire de mise à jour du profil
  onSubmit(): void {
    if (this.profileForm.valid && this.formValuesChanges()) {

      const updateData: any = {};

      // Ajoute uniquement les champs modifiés
      const currentValues = this.profileForm.value;
      
      if (currentValues.username !== this.initialFormValues.username) {
        updateData.username = currentValues.username;
      }
      
      if (currentValues.email !== this.initialFormValues.email) {
        updateData.email = currentValues.email;
      }
      
      if (currentValues.password && currentValues.password.length > 0) {
        updateData.password = currentValues.password;
      }

      this.userService.updateProfile(updateData)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response: AuthResponse) => {
            this.sessionService.logIn(response.token);
            this.loadUserProfile();
            this.snackBar.open("Profil mis à jour avec succès", "Fermer", { duration: 2000 });
          },
          error: (error) => {
            console.error("Erreur lors de la mise à jour du profil:", error);
            this.snackBar.open("Impossible de mettre à jour le profil", "Fermer", { duration: 3000 });
          }
        });
    }
  }

  // Se désabonner d'un topic
  unsubscribe(topicId: number): void {
    this.topicService.unsubscribeFromTopic(topicId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (topics: Topic[]) => {
          this.subscriptions = topics;
          this.loadSubscriptions();
          this.snackBar.open("Vous avez été désabonné avec succès", "Fermer", { duration: 2000 });
        },
        error: (error) => {
          console.error("Erreur lors du désabonnement:", error);
          this.snackBar.open("Impossible de se désabonner", "Fermer", { duration: 3000 });
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}