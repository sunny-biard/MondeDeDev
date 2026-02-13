package com.openclassrooms.mddapi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration CORS (Cross-Origin Resource Sharing).
 * Cette classe configure les politiques CORS pour permettre
 * aux applications frontend de communiquer avec l'API.
 * Configuration actuelle :
 *   Origine autorisée : {@code http://localhost:4200}
 *   Méthodes HTTP autorisées : GET, POST, PUT, DELETE, OPTIONS
 *   Headers autorisés : Tous (*)
 *   Credentials autorisés : Oui
 *   Max age : 3600 secondes
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure les mappings CORS pour l'application.
     * Permet à l'application Angular frontend (port 4200)
     * d'accéder à l'API backend (port 3001).
     * @param registry Registre de configuration CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}