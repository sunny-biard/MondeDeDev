package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.model.entity.User;
import com.openclassrooms.mddapi.model.mapper.AuthMapper;
import com.openclassrooms.mddapi.model.mapper.UserMapper;
import com.openclassrooms.mddapi.model.request.LoginRequest;
import com.openclassrooms.mddapi.model.request.RegisterRequest;
import com.openclassrooms.mddapi.model.response.AuthSuccess;
import com.openclassrooms.mddapi.model.response.UserResponse;
import com.openclassrooms.mddapi.service.JwtService;
import com.openclassrooms.mddapi.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
    public AuthSuccess login(@Valid @RequestBody LoginRequest req) {
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
    public AuthSuccess register(@Valid @RequestBody RegisterRequest req) {
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

    @GetMapping("/me")
    public UserResponse me() {
        // Récupère l'email de l'utilisateur authentifié
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Récupère l'utilisateur en base de données et le convertit en DTO
        return UserMapper.toDto(
            userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"))
        );
    }
}
