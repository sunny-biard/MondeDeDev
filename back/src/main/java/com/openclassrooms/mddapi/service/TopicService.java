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

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    // Récupère un topic par son ID
    public Topic getTopicById(@NonNull Integer id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
    }

    // Récupère tous les topics et les convertit en DTO
    public List<TopicDto> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();

        return topics.stream()
                .map(TopicMapper::toDto)
                .toList();
    }

    // Permet à un utilisateur de s'abonner à un topic
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

    // Permet à un utilisateur de se désabonner d'un topic
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

    // Récupère les topics auxquels un utilisateur est abonné
    public List<TopicDto> getUserSubscriptions(@NonNull Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSubscriptions().stream()
                .map(TopicMapper::toDto)
                .toList();
    }
}