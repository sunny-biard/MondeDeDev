package com.openclassrooms.mddapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration du gestionnaire d'encodage de mots de passe.
 * Cette classe fournit un bean {@link BCryptPasswordEncoder} pour
 * l'encodage sécurisé des mots de passe utilisateurs.
 * Utilisation :
 *   Encodage des mots de passe lors de l'inscription
 *   Vérification des mots de passe lors de la connexion
 *   Mise à jour des mots de passe
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}