package com.openclassrooms.mddapi.model.mapper;

import java.sql.Date;

import org.springframework.lang.NonNull;

import com.openclassrooms.mddapi.model.dto.PostDto;
import com.openclassrooms.mddapi.model.entity.Post;
import com.openclassrooms.mddapi.model.entity.Topic;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.request.PostCreateRequest;

public class PostMapper {

    @NonNull
    public static Post toEntity(@NonNull User user, @NonNull Topic topic, PostCreateRequest req) {
        Post post = new Post();

        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setTopic(topic);
        post.setCreatedAt(new Date(System.currentTimeMillis()));
        post.setUpdatedAt(new Date(System.currentTimeMillis()));
        post.setUser(user);

        return post;
    }

    @NonNull
    public static PostDto toDto(Post post) {
        PostDto postDto = new PostDto();

        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setCreatedAt(post.getCreatedAt());
        postDto.setUpdatedAt(post.getUpdatedAt());
        postDto.setTopic(TopicMapper.toDto(post.getTopic()));
        postDto.setUser(UserMapper.toDto(post.getUser()));

        return postDto;
    }
}