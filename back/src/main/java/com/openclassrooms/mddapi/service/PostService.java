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

/**
 * Service de gestion des posts (articles).
 * Ce service fournit les fonctionnalités suivantes :
 * - Récupération du fil d'actualité personnalisé
 * - Récupération de posts spécifiques
 * - Récupération de posts par thème
 * - Création de nouveaux posts
 */
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TopicService topicService;

    /**
     * Récupère le fil d'actualité personnalisé d'un utilisateur.
     * Retourne tous les posts des thèmes auxquels l'utilisateur
     * est abonné, triés du plus récent au plus ancien par défaut.
     * @param userId Identifiant de l'utilisateur
     * @return Liste de {@link PostDto} triée
     */
    public List<PostDto> getAllPosts(@NonNull Integer userId) {
        List<Post> posts = postRepository.findByUserSubscriptionsDesc(userId);

        return posts.stream()
                .map(PostMapper::toDto)
                .toList();
    }

    /**
     * Récupère le fil d'actualité avec un ordre de tri personnalisé.
     * @param userId Identifiant de l'utilisateur
     * @param sortOrder Ordre de tri ("asc" ou "desc")
     * @return Liste de {@link PostDto} triée selon l'ordre spécifié
     */
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

    /**
     * Récupère un post spécifique par son identifiant.
     * @param id Identifiant du post
     * @return {@link PostDto} du post
     * @throws RuntimeException Si le post n'est pas trouvé
     */
    public PostDto getPostById(@NonNull Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return PostMapper.toDto(post);
    }

    /**
     * Récupère tous les posts d'un thème spécifique.
     * @param topicId Identifiant du thème
     * @return Liste de {@link PostDto} du thème
     * @throws RuntimeException Si le thème n'est pas trouvé
     */
    public List<PostDto> getPostsByTopicId(@NonNull Integer topicId) {
        // Vérifie que le topic existe
        topicService.getTopicById(topicId);

        List<Post> posts = postRepository.findByTopicId(topicId);

        return posts.stream()
                .map(PostMapper::toDto)
                .toList();
    }

    /**
     * Crée un nouveau post.
     * Associe automatiquement l'auteur et le thème au post.
     * @param user Auteur du post
     * @param req Requête de création contenant titre, contenu et thème
     * @return Post créé avec son identifiant
     * @throws RuntimeException Si le thème n'est pas trouvé
     */
    public Post createPost(@NonNull User user, PostCreateRequest req) {
        // Récupère le topic par son ID
        Topic topic = topicService.getTopicById(req.getTopicId());

        Post post = PostMapper.toEntity(user, topic, req);

        return postRepository.save(post);
    }
}