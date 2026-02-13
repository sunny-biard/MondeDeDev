package com.openclassrooms.mddapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.model.entity.User;

/**
 * Repository pour l'accès aux données des utilisateurs.
 * Fournit les méthodes CRUD de base via {@link JpaRepository}
 * plus des méthodes de recherche personnalisées.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
