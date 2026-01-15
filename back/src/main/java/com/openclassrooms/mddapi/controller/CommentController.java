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

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@RequestParam Integer postId) {
        try {
            List<CommentDto> comments = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateRequest req) {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Crée le commentaire
            Comment createdComment = commentService.createComment(user, req);

            CommentDto commentDto = CommentMapper.toDto(createdComment);

            return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer id) {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Supprime le commentaire
            commentService.deleteComment(id, user.getId());

            return ResponseEntity.ok("Comment successfully deleted");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}