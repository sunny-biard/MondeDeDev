import { Component, OnInit } from '@angular/core';
import { PostService } from '../../services/post.service';
import { Post } from '../../interfaces/post.interface';
import { Router } from '@angular/router';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.scss']
})
export class PostsComponent implements OnInit {
  posts: Post[] = [];
  sortDirection: 'asc' | 'desc' = 'desc';

  constructor(
    private postService: PostService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  // Trier les articles par date
  toggleSort() {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.loadPosts();
  }

  // Charge tous les articles disponibles
  private loadPosts(): void {
    this.postService.getAllPosts(this.sortDirection).subscribe({
      next: (posts: Post[]) => {
        this.posts = posts;
      },
      error: (error) => {
        console.error("Erreur lors du chargement des articles:", error);
      }
    });
  }

  // Naviguer vers le détail d'un post
  viewPost(postId: number): void {
    this.router.navigate(['/posts', postId]);
  }

  // Naviguer vers la création d'un post
  createPost(): void {
    this.router.navigate(['/posts/create']);
  }
}
