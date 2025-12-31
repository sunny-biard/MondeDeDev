package com.openclassrooms.mddapi.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.openclassrooms.mddapi.service.JwtService;
import com.openclassrooms.mddapi.service.UserService;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    // Filtre chaque requête pour valider le token JWT
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Récupère le header Authorization
        String username = null;
        String jwt = null;

        // Vérifie que le header existe et commence par "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Récupère le token sans "Bearer "
            try {
                username = jwtService.getUsernameByToken(jwt); // Décode le nom d'utilisateur depuis le token

                // Si l'utilisateur n'est pas encore authentifié dans le contexte Spring
                // Security
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userService.loadUserByUsername(username);

                    // Vérifie si le token est valide
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        System.out.println("Authenticated user: " + username + " for " + request.getMethod() + " "
                                + request.getRequestURI());
                    }
                }
            } catch (Exception e) {
                // Autorise les endpoints d'authentification même si le JWT est invalide
                if (request.getRequestURI().contains("/api/auth/")) {
                    System.out.println("Skipping JWT validation for auth endpoint");
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired JWT token");
                    return; // Stop le traitement si JWT invalide
                }
            }
        }

        chain.doFilter(request, response);
    }
}