package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.mddapi.model.dto.PostDto;
import com.openclassrooms.mddapi.model.entity.Post;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.PostMapper;
import com.openclassrooms.mddapi.model.request.PostCreateRequest;
import com.openclassrooms.mddapi.service.PostService;
import com.openclassrooms.mddapi.service.UserService;

import jakarta.validation.Valid;

/**
 * Contrôleur REST gérant les opérations sur les posts (articles).
 * Ce contrôleur expose les endpoints suivants :
 * - {@code GET /api/posts} - Récupération du fil d'actualité personnalisé
 * - {@code GET /api/posts/:id} - Récupération d'un post spécifique
 * - {@code GET /api/posts/topic/:topicId} - Récupération des posts d'un thème
 * - {@code POST /api/posts} - Création d'un nouveau post
 * Tous les endpoints nécessitent une authentification JWT.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * Récupère le fil d'actualité personnalisé de l'utilisateur.
     * Retourne tous les posts des thèmes auxquels l'utilisateur
     * est abonné, triés par date de création.
     * Paramètres de tri disponibles :
     * - {@code sort=desc} - Du plus récent au plus ancien (défaut)
     * - {@code sort=asc} - Du plus ancien au plus récent
     * @param sort Ordre de tri (asc ou desc), défaut: desc
     * @return {@link ResponseEntity} contenant la liste des posts
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé
     */
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam(required = false, defaultValue = "desc") String sort) {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Récupère le fil personnalisé avec tri (plus récent au plus ancien par défaut)
            List<PostDto> feed = postService.getAllPosts(user.getId(), sort);

            return ResponseEntity.ok(feed);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère un post spécifique par son ID.
     * @param id Identifiant du post
     * @return {@link ResponseEntity} contenant le post
     * @throws RuntimeException Si le post n'est pas trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Integer id) {
        try {
            PostDto post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupère tous les posts d'un thème spécifique.
     * @param topicId Identifiant du thème
     * @return {@link ResponseEntity} contenant la liste des posts du thème
     * @throws RuntimeException Si le thème n'est pas trouvé
     */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<PostDto>> getPostsByTopic(@PathVariable Integer topicId) {
        try {
            List<PostDto> posts = postService.getPostsByTopicId(topicId);
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crée un nouveau post.
     * @param req Requête de création contenant titre, contenu et thème
     * @return {@link ResponseEntity} contenant le post créé (status 201)
     * @throws RuntimeException Si l'utilisateur ou le thème n'est pas trouvé
     */
    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostCreateRequest req) {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Crée le post
            Post createdPost = postService.createPost(user, req);

            return ResponseEntity.status(HttpStatus.CREATED).body(PostMapper.toDto(createdPost));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}