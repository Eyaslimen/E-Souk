package com.example.e_souk.Config;

import com.example.e_souk.Service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification JWT pour Spring Security
 * Intercepte chaque requête pour vérifier et valider les tokens JWT
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    
    /**
     * Méthode principale du filtre
     * Vérifie la présence d'un token JWT valide dans l'en-tête Authorization
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extraction du token depuis l'en-tête Authorization
            String jwt = extractJwtFromRequest(request);
            
            // Validation du token et authentification
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                
                // Extraction du nom d'utilisateur depuis le token
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                
                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    // Chargement des détails utilisateur
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // Création de l'authentification
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, 
                                    null, 
                                    userDetails.getAuthorities()
                            );
                    
                    // Ajout des détails de la requête
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Stockage de l'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("Utilisateur authentifié via JWT: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'authentification JWT: {}", e.getMessage());
            // Ne pas bloquer la requête en cas d'erreur d'authentification
        }
        
        // Continuation de la chaîne de filtres
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extrait le token JWT depuis l'en-tête Authorization
     * @param request Requête HTTP
     * @return Token JWT ou null si non trouvé
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Supprime "Bearer "
        }
        
        return null;
    }
    
    /**
     * Détermine si le filtre doit être appliqué à cette requête
     * @param request Requête HTTP
     * @return true si le filtre doit être appliqué
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Ne pas appliquer le filtre sur les endpoints publics
        return path.startsWith("/api/auth/") || 
               path.startsWith("/swagger-ui/") || 
               path.startsWith("/v3/api-docs/") ||
               path.equals("/swagger-ui.html") ||
               path.equals("/v3/api-docs");
    }
} 