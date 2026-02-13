package com.openclassrooms.mddapi.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import java.util.Date;
import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.configuration.JwtProperties;

/**
 * Service de gestion des tokens JWT (JSON Web Tokens).
 * Ce service fournit les fonctionnalités suivantes :
 * - Génération de tokens JWT
 * - Validation de tokens JWT
 * - Extraction d'informations depuis les tokens
 * Configuration :
 * - Algorithme : HS256 (HMAC avec SHA-256)
 * - Clé secrète : Chargée depuis application.properties
 * - Durée de validité : Configurable (défaut: 1 heure)
 */
@Service
public class JwtService {

    @Autowired
    private JwtProperties jwtProperties;
    
    private Key key;

    /**
     * Initialise la clé de signature JWT.
     * Méthode exécutée après l'injection des dépendances.
     * Crée une clé HMAC SHA-256 à partir du secret configuré.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        System.out.println("JWT loaded. Expiration: " + jwtProperties.getExpiration());
    }

    /**
     * Génère un nouveau token JWT pour un utilisateur.
     * Le token contient :
     * - Subject : Email de l'utilisateur
     * - Date d'émission : Date actuelle
     * - Date d'expiration : Date actuelle + durée de validité
     * @param userDetails Détails de l'utilisateur
     * @return Token JWT signé
     */
    public String generateToken(org.springframework.security.core.userdetails.UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Utilise l'email comme sujet
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date d'émission
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration())) // Date d'expiration
                .signWith(key, SignatureAlgorithm.HS256) // Signature avec la clé et l'algorithme HS256
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur (email) depuis un token JWT.
     * @param token Token JWT à décoder
     * @return Email de l'utilisateur
     * @throws JwtException Si le token est invalide ou expiré
     */
    public String getUsernameByToken(String token) {
        return getAllClaimsByToken(token).getSubject();
    }

    /**
     * Vérifie si un token JWT est expiré.
     * @param token Token JWT à vérifier
     * @return true si le token est expiré, false sinon
     */
    private boolean isTokenExpired(String token) {
        return getAllClaimsByToken(token).getExpiration().before(new Date());
    }

    /**
     * Vérifie si un token JWT est valide pour un utilisateur donné.
     * Un token est valide si :
     * - Le nom d'utilisateur correspond
     * - Le token n'est pas expiré
     * @param token Token JWT à valider
     * @param userDetails Détails de l'utilisateur
     * @return true si le token est valide, false sinon
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameByToken(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Récupère toutes les claims (données) depuis un token JWT.
     * @param token Token JWT à décoder
     * @return Claims du token
     * @throws JwtException Si le token est invalide
     */
    private Claims getAllClaimsByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}