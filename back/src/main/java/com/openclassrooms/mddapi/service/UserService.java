package com.openclassrooms.mddapi.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

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

    // Méthode requise par UserDetailsService pour l'authentification
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Cherche d'abord par email, puis par username
        return userRepository.findByEmail(identifier)
            .or(() -> userRepository.findByUsername(identifier))
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + identifier));
    }
}