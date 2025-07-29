package com.example.e_souk.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration JPA pour activer l'audit automatique des entités
 * Permet l'utilisation des annotations @CreatedDate et @LastModifiedDate
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // Configuration automatique activée par @EnableJpaAuditing
} 