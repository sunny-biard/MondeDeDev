package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.openclassrooms.mddapi.model.dto.CommentDto;
import com.openclassrooms.mddapi.model.entity.Comment;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.CommentMapper;
import com.openclassrooms.mddapi.model.request.CommentCreateRequest;
import com.openclassrooms.mddapi.service.CommentService;
import com.openclassrooms.mddapi.service.UserService;

import jakarta.validation.Valid;

/**
 * Contrôleur REST gérant les opérations sur les commentaires.
 * Ce contrôleur expose les endpoints suivants :
 * - {@code GET /api/comments?postId=X} - Récupération des commentaires d'un post
 * - {@code POST /api/comments} - Création d'un nouveau commentaire
 * Tous les endpoints nécessitent une authentification JWT.
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    /**
     * Récupère tous les commentaires d'un post spécifique.
     * @param postId Identifiant du post
     * @return {@link ResponseEntity} contenant la liste des commentaires
     */
    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@RequestParam Integer postId) {
        try {
            List<CommentDto> comments = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Crée un nouveau commentaire sur un post.
     * @param req Requête de création contenant le contenu et l'ID du post
     * @return {@link ResponseEntity} contenant le commentaire créé (status 201)
     * @throws RuntimeException Si l'utilisateur ou le post n'est pas trouvé
     */
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateRequest req) {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Crée le commentaire
            Comment createdComment = commentService.createComment(user, req);

            return ResponseEntity.status(HttpStatus.CREATED).body(CommentMapper.toDto(createdComment));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}