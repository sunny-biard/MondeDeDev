import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Topic } from '../interfaces/topic.interface';

@Injectable({
    providedIn: 'root'
})
export class TopicService {
    private baseUrl = environment.baseUrl;

    constructor(private http: HttpClient) {}

    // Récupérer tous les topics
    getAllTopics(): Observable<Topic[]> {
        return this.http.get<Topic[]>(`${this.baseUrl}/topics`);
    }

    // S'abonner à un topic
    subscribeToTopic(topicId: number): Observable<Topic[]> {
        return this.http.post<Topic[]>(`${this.baseUrl}/topics/${topicId}/subscribe`, {});
    }

    // Se désabonner d'un topic
    unsubscribeFromTopic(topicId: number): Observable<Topic[]> {
        return this.http.delete<Topic[]>(`${this.baseUrl}/topics/${topicId}/subscribe`);
    }

    // Récupérer les abonnements de l'utilisateur
    getUserSubscriptions(): Observable<Topic[]> {
        return this.http.get<Topic[]>(`${this.baseUrl}/topics/subscriptions`);
    }
}