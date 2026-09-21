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
                // CORS -> Habilitar
                .cors(Customizer.withDefaults())
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

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:5173"));// url front, react
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));// metodos
        configuration.setAllowedHeaders(java.util.List.of("*"));// headers

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
