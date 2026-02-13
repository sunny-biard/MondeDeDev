package com.openclassrooms.mddapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.openclassrooms.mddapi.service.UserService;

/**
 * Configuration de sécurité Spring Security pour l'application.
 * Cette classe configure :
 *   La chaîne de filtres de sécurité
 *   Les endpoints publics et protégés
 *   La gestion de session (stateless pour JWT)
 *   L'authentification basée sur JWT
 * Endpoints publics :
 *   {@code /api/auth/login} - Connexion
 *   {@code /api/auth/register} - Inscription
 * Endpoints protégés : Tous les autres endpoints nécessitent une authentification.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Configure la chaîne de filtres de sécurité.
     * Cette méthode configure :
     *   CORS - Autorise les requêtes cross-origin
     *   CSRF - Désactivé (API stateless)
     *   Autorisation des requêtes
     *   Gestion de session stateless (JWT)
     *   Ajout du filtre JWT avant {@link UsernamePasswordAuthenticationFilter}
     * @param http Objet {@link HttpSecurity} à configurer
     * @return La chaîne de filtres de sécurité configurée
     * @throws Exception Si une erreur survient lors de la configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll() // Endpoints publics
                        .anyRequest().authenticated()) // Tout le reste nécessite une authentification
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure le fournisseur d'authentification.
     * Utilise {@link DaoAuthenticationProvider} avec :
     *   {@link UserService} pour charger les détails utilisateur
     *   {@link BCryptPasswordEncoder} pour vérifier les mots de passe
     * @return Le fournisseur d'authentification configuré
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(this.userService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }

    /**
     * Expose le gestionnaire d'authentification Spring Security.
     * @param config Configuration d'authentification
     * @return Le gestionnaire d'authentification
     * @throws Exception Si une erreur survient lors de la récupération
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}