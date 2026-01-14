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

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    // Récupère tous les commentaires d'un post spécifique
    public List<CommentDto> getCommentsByPostId(@NonNull Integer postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    // Crée un nouveau commentaire sur un post
    public Comment createComment(@NonNull User user, CommentCreateRequest req) {
        // Vérifie que le post existe
        Post post = postRepository.findById(req.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = CommentMapper.toEntity(user, post, req);

        return commentRepository.save(comment);
    }

    // Supprime un commentaire (uniquement par son auteur)
    public void deleteComment(@NonNull Integer commentId, @NonNull Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Vérifie que l'utilisateur est bien l'auteur du commentaire
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}