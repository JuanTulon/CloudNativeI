package com.digitalfix_bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configuracionCadenasFiltrosSeguridad(HttpSecurity http) throws Exception {
        http
                // CSRF (Cross-Site Request Forgery) -> Desactivar
                .csrf(AbstractHttpConfigurer::disable)
                // Session Management (Gestión de Sesiones) -> Sin estado ( Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Autorización (Authorization) -> Cualquier request authenticated (autenticado)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // OAuth2 Resource Server (Validación de JWT) -> Con default config
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
