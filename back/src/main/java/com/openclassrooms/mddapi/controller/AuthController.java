package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.AuthMapper;
import com.openclassrooms.mddapi.model.mapper.UserMapper;
import com.openclassrooms.mddapi.model.request.LoginRequest;
import com.openclassrooms.mddapi.model.request.RegisterRequest;
import com.openclassrooms.mddapi.model.request.UserUpdateRequest;
import com.openclassrooms.mddapi.model.response.AuthResponse;
import com.openclassrooms.mddapi.service.JwtService;
import com.openclassrooms.mddapi.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        // Authentification de l'utilisateur
        authManager
                .authenticate(new UsernamePasswordAuthenticationToken(req.getIdentifier(), req.getPassword()));

        // Génération du token JWT
        UserDetails userDetails = userService.loadUserByUsername(req.getIdentifier());
        String token = jwtService.generateToken(userDetails);

        // Retourne le token dans la réponse
        return AuthMapper.toResponse(token);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        // Vérifie si l'utilisateur existe déjà
        if (userService.getUserByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Vérifie aussi le username
        if (userService.getUserByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username déjà utilisé");
        }

        // Création de l'utilisateur
        User user = UserMapper.toEntity(req, passwordEncoder);

        // Sauvegarde de l'utilisateur en base de données
        userService.saveUser(user);

        // Génération du token JWT
        UserDetails userDetails = userService.loadUserByUsername(req.getEmail());
        String token = jwtService.generateToken(userDetails);

        // Retourne le token dans la réponse
        return AuthMapper.toResponse(token);
    }

    // Récupère le profil de l'utilisateur connecté
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Convertit en DTO avec les abonnements
            return ResponseEntity.ok(UserMapper.toDtoWithSubscriptions(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Met à jour le profil de l'utilisateur connecté
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateRequest req) {
        try {
            // Récupère l'utilisateur authentifié
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Met à jour le profil
            User updatedUser = userService.updateUser(user.getId(), req);

            // Retourne le profil mis à jour avec les abonnements
            return ResponseEntity.ok(UserMapper.toDtoWithSubscriptions(updatedUser));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already taken") || e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
