package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.model.dto.CommentDto;
import com.openclassrooms.mddapi.model.entity.Comment;
import com.openclassrooms.mddapi.model.entity.Post;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.CommentMapper;
import com.openclassrooms.mddapi.model.request.CommentCreateRequest;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;

/**
 * Service de gestion des commentaires.
 * Ce service fournit les fonctionnalités suivantes :
 * - Récupération des commentaires d'un post
 * - Création de nouveaux commentaires
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * Récupère tous les commentaires d'un post spécifique.
     * @param postId Identifiant du post
     * @return Liste de {@link CommentDto} triée par date de création
     */
    public List<CommentDto> getCommentsByPostId(@NonNull Integer postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    /**
     * Crée un nouveau commentaire sur un post.
     * Associe automatiquement l'auteur et le post au commentaire.
     * @param user Auteur du commentaire
     * @param req Requête contenant le contenu et l'ID du post
     * @return Commentaire créé avec son identifiant
     * @throws RuntimeException Si le post n'est pas trouvé
     */
    public Comment createComment(@NonNull User user, CommentCreateRequest req) {
        // Vérifie que le post existe
        Post post = postRepository.findById(req.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = CommentMapper.toEntity(user, post, req);

        return commentRepository.save(comment);
    }
}