package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.model.dto.PostDto;
import com.openclassrooms.mddapi.model.entity.Post;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.PostMapper;
import com.openclassrooms.mddapi.model.request.PostCreateRequest;
import com.openclassrooms.mddapi.model.request.PostUpdateRequest;
import com.openclassrooms.mddapi.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostDto getPostById(@NonNull Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return new PostMapper().toDto(post);
    }

    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
            .map(new PostMapper()::toDto)
            .toList();
    }

    public Post createPost(@NonNull User user, PostCreateRequest req) {
        Post post = new PostMapper().toEntity(user, req);

        return postRepository.save(post);
    }

    public Post updatePost(@NonNull Integer id, @NonNull Integer userId, PostUpdateRequest req) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!existingPost.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this post");
        } else {
            if (req.getTitle() != null && !req.getTitle().isEmpty()) {
                existingPost.setTitle(req.getTitle());
            }
            if (req.getContent() != null && !req.getContent().isEmpty()) {
                existingPost.setContent(req.getContent());
            }
            if (req.getTopic() != null) {
                existingPost.setTopic(req.getTopic());
            }
            existingPost.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
        }

        return postRepository.save(existingPost);
    }

    public void deletePost(@NonNull Integer id, @NonNull Integer userId) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!existingPost.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this post");
        } else {
            postRepository.delete(existingPost);
        }
    }
}
