package com.openclassrooms.mddapi.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.request.UserUpdateRequest;
import com.openclassrooms.mddapi.repository.UserRepository;

/**
 * Service de gestion des utilisateurs.
 * Implémente {@link UserDetailsService} pour l'intégration
 * avec Spring Security.
 * Ce service fournit les fonctionnalités suivantes :
 * - Récupération d'utilisateurs
 * - Création d'utilisateurs
 * - Mise à jour du profil utilisateur
 * - Chargement des détails utilisateur pour l'authentification
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Récupère un utilisateur par son email.
     * @param email Adresse email de l'utilisateur
     * @return {@link Optional} contenant l'utilisateur si trouvé
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur.
     * @param username Nom d'utilisateur
     * @return {@link Optional} contenant l'utilisateur si trouvé
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Récupère un utilisateur par son identifiant.
     * @param id Identifiant de l'utilisateur
     * @return {@link Optional} contenant l'utilisateur si trouvé
     */
    public Optional<User> getUserById(@NonNull Integer id) {
        return userRepository.findById(id);
    }

    /**
     * Enregistre un nouvel utilisateur.
     * Note : Le mot de passe doit être encodé avant l'appel.
     * @param user Utilisateur à créer
     * @return Utilisateur créé avec son ID
     */
    public User createUser(@NonNull User user) {
        return userRepository.save(user);
    }

    /**
     * Met à jour le profil d'un utilisateur.
     * Seuls les champs fournis dans la requête sont mis à jour.
     * Vérifie l'unicité de l'email et du nom d'utilisateur.
     * @param userId Identifiant de l'utilisateur à modifier
     * @param req Requête contenant les nouvelles informations
     * @return Utilisateur mis à jour
     * @throws RuntimeException Si l'utilisateur n'existe pas ou
     *                          si email/username déjà pris
     */
    public User updateUser(@NonNull Integer userId, UserUpdateRequest req) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasChanges = false;

        // Mise à jour du username
        if (req.getUsername() != null && !req.getUsername().isEmpty()
                && !req.getUsername().equals(existingUser.getActualUsername())) {

            // Vérifie que le nouveau username n'est pas déjà pris
            Optional<User> userWithSameUsername = userRepository.findByUsername(req.getUsername());
            if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(userId)) {
                throw new RuntimeException("Username already taken");
            }

            existingUser.setUsername(req.getUsername());
            hasChanges = true;
        }

        // Mise à jour de l'email
        if (req.getEmail() != null && !req.getEmail().isEmpty()
                && !req.getEmail().equals(existingUser.getEmail())) {

            // Vérifie que le nouvel email n'est pas déjà pris
            Optional<User> userWithSameEmail = userRepository.findByEmail(req.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(userId)) {
                throw new RuntimeException("Email already taken");
            }

            existingUser.setEmail(req.getEmail());
            hasChanges = true;
        }

        // Mise à jour du mot de passe
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(req.getPassword()));
            hasChanges = true;
        }

        // Met à jour la date de modification si des changements ont été effectués
        if (hasChanges) {
            existingUser.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
            return userRepository.save(existingUser);
        }

        return existingUser;
    }

    /**
     * Charge les détails d'un utilisateur par son identifiant.
     * Implémentation requise par {@link UserDetailsService}.
     * Accepte l'email OU le nom d'utilisateur comme identifiant.
     * @param identifier Email ou nom d'utilisateur
     * @return Détails de l'utilisateur pour Spring Security
     * @throws UsernameNotFoundException Si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Cherche d'abord par email, puis par username
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + identifier));
    }
}