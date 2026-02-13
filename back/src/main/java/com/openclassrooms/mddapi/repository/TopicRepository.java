package com.openclassrooms.mddapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.model.entity.Topic;

/**
 * Repository pour l'accès aux données des thèmes.
 * Utilise uniquement les méthodes CRUD de base
 * fournies par {@link JpaRepository}.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
}