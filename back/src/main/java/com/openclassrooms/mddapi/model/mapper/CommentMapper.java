package com.openclassrooms.mddapi.model.mapper;

import java.sql.Date;

import org.springframework.lang.NonNull;

import com.openclassrooms.mddapi.model.dto.CommentDto;
import com.openclassrooms.mddapi.model.entity.Comment;
import com.openclassrooms.mddapi.model.entity.Post;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.request.CommentCreateRequest;

/**
 * Mapper pour la conversion entre entités Comment et DTOs.
 */
public class CommentMapper {

    @NonNull
    public static Comment toEntity(@NonNull User user, @NonNull Post post, CommentCreateRequest req) {
        Comment comment = new Comment();

        comment.setContent(req.getContent());
        comment.setCreatedAt(new Date(System.currentTimeMillis()));
        comment.setUpdatedAt(new Date(System.currentTimeMillis()));
        comment.setUser(user);
        comment.setPost(post);

        return comment;
    }

    @NonNull
    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setUser(UserMapper.toDto(comment.getUser()));

        return commentDto;
    }
}