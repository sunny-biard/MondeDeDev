import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostService } from '../../services/post.service';
import { SessionService } from '../../services/session.service';
import { Post } from '../../interfaces/post.interface';
import { Comment } from '../../interfaces/comment.interface';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-post-details',
  templateUrl: './post-details.component.html',
  styleUrls: ['./post-details.component.scss']
})
export class PostDetailsComponent implements OnInit {
  post: Post | null = null;
  comments: Comment[] = [];
  commentForm!: FormGroup;
  loading: boolean = true;
  currentUserId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private postService: PostService,
    private sessionService: SessionService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCurrentUser();
    this.loadPostDetail();
  }

  // Initialiser le formulaire de commentaire
  private initForm(): void {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  // Charger l'utilisateur actuel
  private loadCurrentUser(): void {
    const user = this.sessionService.getUser();
    if (user) {
      this.currentUserId = user.id;
    }
  }

  // Charger les détails du post
  private loadPostDetail(): void {
    const postId = Number(this.route.snapshot.paramMap.get('id'));
    
    if (!postId) {
      this.router.navigate(['/posts']);
      return;
    }

    this.postService.getPostById(postId).subscribe({
      next: (post: Post) => {
        this.post = post;
        this.loadComments(postId);
      },
      error: (error) => {
        console.error("Erreur lors du chargement du post:", error);
        this.snackBar.open("Impossible de charger l'article", "Fermer", { duration: 3000 });
        this.router.navigate(['/posts']);
      }
    });
  }

  // Charger les commentaires
  private loadComments(postId: number): void {
    this.postService.getCommentsByPostId(postId).subscribe({
      next: (comments: Comment[]) => {
        this.comments = comments;
        this.loading = false;
      },
      error: (error) => {
        console.error("Erreur lors du chargement des commentaires:", error);
        this.loading = false;
      }
    });
  }

  // Soumettre un commentaire
  onSubmitComment(): void {
    if (this.commentForm.valid && this.post) {
      const commentData = {
        content: this.commentForm.value.content,
        postId: this.post.id
      };

      this.postService.createComment(commentData).subscribe({
        next: (comment: Comment) => {
          this.comments.push(comment);
          this.commentForm.reset();
          this.snackBar.open("Commentaire ajouté avec succès", "Fermer", { duration: 2000 });
        },
        error: (error) => {
          console.error("Erreur lors de l'ajout du commentaire:", error);
          this.snackBar.open("Impossible d'ajouter le commentaire", "Fermer", { duration: 3000 });
        }
      });
    }
  }

  // Retour à la liste des posts
  goBack(): void {
    this.router.navigate(['/posts']);
  }
}