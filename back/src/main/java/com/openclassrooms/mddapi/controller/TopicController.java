package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.mddapi.model.dto.TopicDto;
import com.openclassrooms.mddapi.model.entity.Topic;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.TopicMapper;
import com.openclassrooms.mddapi.service.TopicService;
import com.openclassrooms.mddapi.service.UserService;

/**
 * Contrôleur REST gérant les opérations sur les thèmes (topics).
 * Ce contrôleur expose les endpoints suivants :
 * - {@code GET /api/topics} - Récupération de tous les thèmes
 * - {@code GET /api/topics/:id} - Récupération d'un thème spécifique
 * - {@code POST /api/topics/:id/subscribe} - Abonnement à un thème
 * - {@code DELETE /api/topics/:id/subscribe} - Désabonnement d'un thème
 * - {@code GET /api/topics/subscriptions} - Récupération des abonnements
 * Tous les endpoints nécessitent une authentification JWT.
 */
@RestController
@RequestMapping("/api/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    /**
     * Récupère tous les thèmes disponibles.
     * @return {@link ResponseEntity} contenant la liste de tous les thèmes
     */
    @GetMapping
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        List<TopicDto> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    /**
     * Récupère un thème spécifique par son ID.
     * @param id Identifiant du thème
     * @return {@link ResponseEntity} contenant le thème
     * @throws RuntimeException Si le thème n'est pas trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicDto> getTopicById(@PathVariable Integer id) {
        try {
            Topic topic = topicService.getTopicById(id);
            return ResponseEntity.ok(TopicMapper.toDto(topic));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Permet à l'utilisateur de s'abonner à un thème.
     * @param id Identifiant du thème
     * @return {@link ResponseEntity} contenant la liste mise à jour des abonnements
     * @throws RuntimeException Si l'utilisateur/thème n'existe pas ou déjà abonné
     */
    @PostMapping("/{id}/subscribe")
    public ResponseEntity<?> subscribeToTopic(@PathVariable Integer id) {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Abonne l'utilisateur au topic
            topicService.subscribeToTopic(user.getId(), id);

            // Retourne la liste des abonnements mise à jour
            List<TopicDto> subscriptions = topicService.getUserSubscriptions(user.getId());
            return ResponseEntity.ok(subscriptions);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Already subscribed")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Permet à l'utilisateur de se désabonner d'un thème.
     * @param id Identifiant du thème
     * @return {@link ResponseEntity} contenant la liste mise à jour des abonnements
     * @throws RuntimeException Si l'utilisateur/thème n'existe pas ou pas abonné
     */
    @DeleteMapping("/{id}/subscribe")
    public ResponseEntity<?> unsubscribeFromTopic(@PathVariable Integer id) {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Désabonne l'utilisateur du topic
            topicService.unsubscribeFromTopic(user.getId(), id);

            // Retourne la liste des abonnements mise à jour
            List<TopicDto> subscriptions = topicService.getUserSubscriptions(user.getId());
            return ResponseEntity.ok(subscriptions);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Not subscribed")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Récupère tous les thèmes auxquels l'utilisateur est abonné.
     * @return {@link ResponseEntity} contenant la liste des abonnements
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<List<TopicDto>> getUserSubscriptions() {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<TopicDto> subscriptions = topicService.getUserSubscriptions(user.getId());
            return ResponseEntity.ok(subscriptions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}