package com.openclassrooms.mddapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.model.dto.TopicDto;
import com.openclassrooms.mddapi.model.entity.Topic;
import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.TopicMapper;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;

/**
 * Service de gestion des thèmes (topics).
 * Ce service fournit les fonctionnalités suivantes :
 * - Récupération des thèmes
 * - Abonnement et désabonnement aux thèmes
 * - Gestion des abonnements utilisateur
 */
@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Récupère un thème par son identifiant.
     * @param id Identifiant du thème
     * @return Entité {@link Topic}
     * @throws RuntimeException Si le thème n'est pas trouvé
     */
    public Topic getTopicById(@NonNull Integer id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
    }

    /**
     * Récupère tous les thèmes disponibles.
     * @return Liste de {@link TopicDto}
     */
    public List<TopicDto> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();

        return topics.stream()
                .map(TopicMapper::toDto)
                .toList();
    }

    /**
     * Abonne un utilisateur à un thème.
     * Vérifie que l'utilisateur n'est pas déjà abonné.
     * @param userId Identifiant de l'utilisateur
     * @param topicId Identifiant du thème
     * @throws RuntimeException Si utilisateur/thème non trouvé ou déjà abonné
     */
    public void subscribeToTopic(@NonNull Integer userId, @NonNull Integer topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Topic topic = getTopicById(topicId);

        // Vérifie si l'utilisateur n'est pas déjà abonné
        if (user.getSubscriptions().contains(topic)) {
            throw new RuntimeException("Already subscribed to this topic");
        }

        user.getSubscriptions().add(topic);
        userRepository.save(user);
    }

    /**
     * Désabonne un utilisateur d'un thème.
     * Vérifie que l'utilisateur est bien abonné.
     * @param userId Identifiant de l'utilisateur
     * @param topicId Identifiant du thème
     * @throws RuntimeException Si utilisateur/thème non trouvé ou pas abonné
     */
    public void unsubscribeFromTopic(@NonNull Integer userId, @NonNull Integer topicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Topic topic = getTopicById(topicId);

        // Vérifie si l'utilisateur est bien abonné
        if (!user.getSubscriptions().contains(topic)) {
            throw new RuntimeException("Not subscribed to this topic");
        }

        user.getSubscriptions().remove(topic);
        userRepository.save(user);
    }

    /**
     * Récupère les thèmes auxquels un utilisateur est abonné.
     * @param userId Identifiant de l'utilisateur
     * @return Liste de {@link TopicDto} des abonnements
     * @throws RuntimeException Si l'utilisateur n'est pas trouvé
     */
    public List<TopicDto> getUserSubscriptions(@NonNull Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSubscriptions().stream()
                .map(TopicMapper::toDto)
                .toList();
    }
}