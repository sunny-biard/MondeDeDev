import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PostService } from '../../services/post.service';
import { TopicService } from '../../services/topic.service';
import { Topic } from '../../interfaces/topic.interface';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-create-post',
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.scss']
})
export class CreatePostComponent implements OnInit {
  postForm!: FormGroup;
  topics: Topic[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private topicService: TopicService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadTopics();
  }

  // Initialiser le formulaire
  private initForm(): void {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      topicId: ['', Validators.required],
      content: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  // Charger les topics disponibles
  private loadTopics(): void {
    this.topicService.getAllTopics()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (topics: Topic[]) => {
          this.topics = topics;
        },
        error: (error) => {
          console.error("Erreur lors du chargement des thèmes:", error);
        }
      });
  }

  // Soumettre le formulaire
  onSubmit(): void {
    if (this.postForm.valid) {
      this.postService.createPost(this.postForm.value)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (post) => {
            this.snackBar.open("Article créé avec succès", "Fermer", { duration: 2000 });
            this.router.navigate(['/posts', post.id]);
          },
          error: (error) => {
            console.error("Erreur lors de la création de l'article:", error);
            this.snackBar.open("Impossible de créer l'article", "Fermer", { duration: 3000 });
          }
        });
    }
  }

  // Retourner à la liste
  goBack(): void {
    this.router.navigate(['/posts']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}