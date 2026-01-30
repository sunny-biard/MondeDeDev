import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Post } from '../interfaces/post.interface';
import { Comment } from '../interfaces/comment.interface';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private baseUrl = environment.baseUrl;

  constructor(private http: HttpClient) {}

    // Récupérer tous les posts
    getAllPosts(sort: 'asc' | 'desc'): Observable<Post[]> {
        return this.http.get<Post[]>(`${this.baseUrl}/posts?sort=${sort}`);
    }

    // Récupérer un post par son ID
    getPostById(id: number): Observable<Post> {
        return this.http.get<Post>(`${this.baseUrl}/posts/${id}`);
    }

    // Créer un nouveau post
    createPost(data: { title: string; content: string; topicId: number }): Observable<Post> {
        return this.http.post<Post>(`${this.baseUrl}/posts`, data);
    }

    // Récupérer les commentaires d'un post
    getCommentsByPostId(postId: number): Observable<Comment[]> {
        return this.http.get<Comment[]>(`${this.baseUrl}/comments?postId=${postId}`);
    }

    // Créer un commentaire
    createComment(data: { content: string; postId: number }): Observable<Comment> {
        return this.http.post<Comment>(`${this.baseUrl}/comments`, data);
    }
}