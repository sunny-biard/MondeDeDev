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

@Service
public class JwtService {

    @Autowired
    private JwtProperties jwtProperties;
    
    private Key key;

    // Initialise la clé de signature à partir du secret configuré
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        System.out.println("JWT loaded. Expiration: " + jwtProperties.getExpiration());
    }

    // Génère un token JWT pour un utilisateur donné
    public String generateToken(org.springframework.security.core.userdetails.UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Utilise l'email comme sujet
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date d'émission
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration())) // Date d'expiration
                .signWith(key, SignatureAlgorithm.HS256) // Signature avec la clé et l'algorithme HS256
                .compact();
    }

    // Récupère le nom d'utilisateur depuis un token JWT
    public String getUsernameByToken(String token) {
        return getAllClaimsByToken(token).getSubject();
    }

    // Vérifie si le token est expiré
    private boolean isTokenExpired(String token) {
        return getAllClaimsByToken(token).getExpiration().before(new Date());
    }

    // Vérifie si le token est valide pour un utilisateur donné
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameByToken(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    // Récupère toutes les réclamations (claims) depuis un token JWT
    private Claims getAllClaimsByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}