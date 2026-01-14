package com.openclassrooms.mddapi.model.mapper;

import java.sql.Date;

import org.springframework.lang.NonNull;

import com.openclassrooms.mddapi.model.dto.PostDto;
import com.openclassrooms.mddapi.model.entity.Post;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.request.PostCreateRequest;

public class PostMapper {

    @Autowired
    private TopicService topicService;

    public Post toEntity(@NonNull User user, PostCreateRequest req) {
        Post post = new Post();

        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setTopic(topicService.getTopicById(req.getTopicId()));
        post.setCreatedAt(new Date(System.currentTimeMillis()));
        post.setUpdatedAt(new Date(System.currentTimeMillis()));
        post.setUser(user);

        return post;
    }

    public PostDto toDto(Post post) {
        PostDto postDto = new PostDto();

        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setCreatedAt(post.getCreatedAt());
        postDto.setUpdatedAt(post.getUpdatedAt());
        postDto.setTopic(post.getTopic());
        postDto.setUser(UserMapper.toDto(post.getUser()));

        return postDto;
    }
}
