// package com.example.e_souk.Config;

// import io.swagger.v3.oas.models.Components;
// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Contact;
// import io.swagger.v3.oas.models.info.Info;
// import io.swagger.v3.oas.models.info.License;
// import io.swagger.v3.oas.models.security.SecurityRequirement;
// import io.swagger.v3.oas.models.security.SecurityScheme;
// import io.swagger.v3.oas.models.servers.Server;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import java.util.List;

// /**
//  * Configuration Swagger/OpenAPI 3 pour la documentation de l'API
//  * Configure l'interface de documentation interactive
//  */
// @Configuration
// public class SwaggerConfig {
    
//     /**
//      * Configuration de l'API OpenAPI
//      * @return Configuration OpenAPI
//      */
//     @Bean
//     public OpenAPI customOpenAPI() {
//         return new OpenAPI()
//                 // Informations générales de l'API
//                 .info(new Info()
//                         .title("ESouk API - Plateforme E-commerce")
//                         .description("""
//                             API REST pour la plateforme e-commerce ESouk.
                            
//                             ## Fonctionnalités principales :
//                             - **Authentification JWT** : Inscription, connexion, gestion des tokens
//                             - **Gestion des utilisateurs** : Profils, rôles (CLIENT, VENDOR, ADMIN)
//                             - **Gestion des boutiques** : Création et gestion des boutiques vendeurs
//                             - **Gestion des produits** : Catalogue, variantes, attributs
//                             - **Panier et commandes** : Gestion du panier et processus de commande
//                             - **Avis et notations** : Système de reviews pour produits et boutiques
//                             - **Notifications** : Système de notifications en temps réel
                            
//                             ## Authentification :
//                             L'API utilise l'authentification JWT (JSON Web Token).
//                             Pour les endpoints protégés, incluez le token dans l'en-tête Authorization :
//                             `Authorization: Bearer <votre_token_jwt>`
                            
//                             ## Rôles utilisateur :
//                             - **CLIENT** : Peut acheter, laisser des avis, suivre des boutiques
//                             - **VENDOR** : Peut créer des boutiques et vendre des produits
//                             - **ADMIN** : Accès complet à toutes les fonctionnalités
//                             """)
//                         .version("1.0.0")
//                         .contact(new Contact()
//                                 .name("Équipe ESouk")
//                                 .email("contact@esouk.com")
//                                 .url("https://esouk.com"))
//                         .license(new License()
//                                 .name("MIT License")
//                                 .url("https://opensource.org/licenses/MIT")))
                
//                 // Serveurs disponibles
//                 .servers(List.of(
//                         new Server()
//                                 .url("http://localhost:8080")
//                                 .description("Serveur de développement local"),
//                         new Server()
//                                 .url("https://api.esouk.com")
//                                 .description("Serveur de production")
//                 ))
                
//                 // Configuration de sécurité JWT
//                 .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
//                 .components(new Components()
//                         .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
//     }
    
//     /**
//      * Crée le schéma de sécurité pour l'authentification Bearer
//      * @return SecurityScheme pour JWT Bearer
//      */
//     private SecurityScheme createAPIKeyScheme() {
//         return new SecurityScheme()
//                 .type(SecurityScheme.Type.HTTP)
//                 .bearerFormat("JWT")
//                 .scheme("bearer")
//                 .description("""
//                     JWT (JSON Web Token) pour l'authentification.
                    
//                     ## Comment obtenir un token :
//                     1. **Inscription** : POST /api/auth/register
//                     2. **Connexion** : POST /api/auth/login
                    
//                     ## Utilisation :
//                     Incluez le token dans l'en-tête Authorization :
//                     `Authorization: Bearer <votre_token_jwt>`
                    
//                     ## Expiration :
//                     Les tokens expirent après 24 heures.
//                     """);
//     }
// } 