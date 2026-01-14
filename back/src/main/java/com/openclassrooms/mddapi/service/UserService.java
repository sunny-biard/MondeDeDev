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

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Enregistre un nouvel utilisateur
    public User saveUser(@NonNull User user) {
        return userRepository.save(user);
    }

    // Récupère un utilisateur par son email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Récupère un utilisateur par son username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Récupère un utilisateur par son ID
    public Optional<User> getUserById(@NonNull Integer id) {
        return userRepository.findById(id);
    }

    // Met à jour le profil d'un utilisateur
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

    // Méthode requise par UserDetailsService pour l'authentification
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Cherche d'abord par email, puis par username
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + identifier));
    }
}