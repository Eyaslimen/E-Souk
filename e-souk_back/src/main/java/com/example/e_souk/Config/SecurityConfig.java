package com.example.e_souk.Config;

import com.example.e_souk.Service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration complète de Spring Security
 * Configure l'authentification JWT, les autorisations et CORS
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * Configuration de la chaîne de filtres de sécurité
     * @param http Configuration HTTP Security
     * @return SecurityFilterChain configuré
     * @throws Exception en cas d'erreur de configuration
     */
   @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
    http
        // Désactive CSRF car on utilise JWT (stateless)
        .csrf(AbstractHttpConfigurer::disable)
        
        // Configuration CORS
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        
        // Configuration des sessions (stateless pour JWT)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        
        // Configuration des autorisations
        .authorizeHttpRequests(auth -> auth
            // Endpoints publics (pas d'authentification requise)
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/v3/api-docs/**", "/v3/api-docs").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            
            // IMPORTANT
       .requestMatchers("/uploads/**").permitAll()
.requestMatchers("/static/**").permitAll()
.requestMatchers("/*.jpg", "/*.jpeg", "/*.png", "/*.gif", "/*.svg").permitAll()
.requestMatchers("/favicon.ico").permitAll()
.requestMatchers("/error").permitAll()

            // Endpoints protégés par rôle
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/vendor/**").hasAnyRole("VENDOR", "ADMIN")
            .requestMatchers("/api/client/**").hasAnyRole("CLIENT", "VENDOR", "ADMIN")
            
            // Tous les autres endpoints nécessitent une authentification
            .anyRequest().authenticated()
        )
        
        // Configuration du provider d'authentification
        .authenticationProvider(authenticationProvider())
        
        // Ajout du filtre JWT avant le filtre d'authentification par nom d'utilisateur/mot de passe
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
    
    /**
     * Configuration CORS pour permettre les requêtes cross-origin
     * @return CorsConfigurationSource configuré
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origines autorisées (en production, spécifier les domaines exacts)
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // En-têtes autorisés
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept", 
            "Origin", 
            "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        
        // En-têtes exposés
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // Autorise les credentials (cookies, etc.)
        configuration.setAllowCredentials(true);
        
        // Durée de cache des preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Provider d'authentification DAO
     * @return DaoAuthenticationProvider configuré
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // Service de détails utilisateur
        authProvider.setUserDetailsService(userDetailsService);
        
        // Encodeur de mot de passe
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }
    
    /**
     * Encodeur de mot de passe BCrypt
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Gestionnaire d'authentification
     * @param authConfig Configuration d'authentification
     * @return AuthenticationManager
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
} 