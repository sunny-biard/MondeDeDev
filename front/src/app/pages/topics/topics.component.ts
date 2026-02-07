import { Component, OnInit } from '@angular/core';
import { Topic } from '../../interfaces/topic.interface';
import { TopicService } from '../../services/topic.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-topics',
  templateUrl: './topics.component.html',
  styleUrls: ['./topics.component.scss']
})
export class TopicsComponent implements OnInit {
  topics: Topic[] = [];
  subscribedTopicIds: number[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private topicService: TopicService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadTopics();
    this.loadUserSubscriptions();
  }

  // Charge tous les thèmes disponibles
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

  // Charge les abonnements de l'utilisateur
  private loadUserSubscriptions(): void {
    this.topicService.getUserSubscriptions()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (topics: Topic[]) => {
          this.subscribedTopicIds = topics.map(topic => topic.id);
        },
        error: (error) => {
          console.error("Erreur lors du chargement des abonnements:", error);
        }
      });
  }

  // Vérifie si l'utilisateur est abonné à un thème
  isSubscribed(topicId: number): boolean {
    return this.subscribedTopicIds.includes(topicId);
  }

  // Gère l'abonnement à un thème
  subscribe(topicId: number): void {
    if (!this.isSubscribed(topicId)) {
      this.topicService.subscribeToTopic(topicId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (topics: Topic[]) => {
            this.subscribedTopicIds = topics.map(topic => topic.id);
            this.snackBar.open("Vous avez été abonné à ce thème avec succès", "Fermer", { duration: 2000 });
          },
          error: (error) => {
            console.error("Erreur lors de l'abonnement:", error);
          }
        });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}