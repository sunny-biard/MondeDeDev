package com.openclassrooms.mddapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.model.entity.Post;

/**
 * Repository pour l'accès aux données des posts.
 * Fournit des méthodes personnalisées pour récupérer
 * le fil d'actualité personnalisé des utilisateurs.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // Récupère tous les posts des topics auxquels l'utilisateur est abonné (du plus
    // récent au plus ancien)
    @Query("SELECT p FROM Post p " +
            "WHERE p.topic IN " +
            "(SELECT t FROM User u JOIN u.subscriptions t WHERE u.id = :userId) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByUserSubscriptionsDesc(@Param("userId") Integer userId);

    // Récupère tous les posts des topics auxquels l'utilisateur est abonné (du plus
    // ancien au plus récent)
    @Query("SELECT p FROM Post p " +
            "WHERE p.topic IN " +
            "(SELECT t FROM User u JOIN u.subscriptions t WHERE u.id = :userId) " +
            "ORDER BY p.createdAt ASC")
    List<Post> findByUserSubscriptionsAsc(@Param("userId") Integer userId);

    // Récupère tous les posts d'un topic spécifique
    List<Post> findByTopicId(Integer topicId);
}