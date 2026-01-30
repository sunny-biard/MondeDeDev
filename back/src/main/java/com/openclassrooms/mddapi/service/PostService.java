package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.model.dto.PostDto;
import com.openclassrooms.mddapi.model.entity.Post;
import com.openclassrooms.mddapi.model.entity.Topic;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.PostMapper;
import com.openclassrooms.mddapi.model.request.PostCreateRequest;
import com.openclassrooms.mddapi.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TopicService topicService;

    // Récupère le fil d'actualité personnalisé de l'utilisateur
    public List<PostDto> getAllPosts(@NonNull Integer userId) {
        List<Post> posts = postRepository.findByUserSubscriptionsDesc(userId);

        return posts.stream()
                .map(PostMapper::toDto)
                .toList();
    }

    // Récupère le fil d'actualité personnalisé de l'utilisateur avec possibilité de
    // choisir l'ordre de tri
    public List<PostDto> getAllPosts(@NonNull Integer userId, @NonNull String sortOrder) {
        List<Post> posts;

        if (sortOrder.equalsIgnoreCase("asc")) {
            posts = postRepository.findByUserSubscriptionsAsc(userId);
        } else {
            posts = postRepository.findByUserSubscriptionsDesc(userId);
        }

        return posts.stream()
                .map(PostMapper::toDto)
                .toList();
    }

    // Récupère un post par son ID et le convertit en DTO
    public PostDto getPostById(@NonNull Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return PostMapper.toDto(post);
    }

    // Récupère tous les posts d'un topic spécifique
    public List<PostDto> getPostsByTopicId(@NonNull Integer topicId) {
        // Vérifie que le topic existe
        topicService.getTopicById(topicId);

        List<Post> posts = postRepository.findByTopicId(topicId);

        return posts.stream()
                .map(PostMapper::toDto)
                .toList();
    }

    // Crée un nouveau post
    public Post createPost(@NonNull User user, PostCreateRequest req) {
        // Récupère le topic par son ID
        Topic topic = topicService.getTopicById(req.getTopicId());

        Post post = PostMapper.toEntity(user, topic, req);

        return postRepository.save(post);
    }
}