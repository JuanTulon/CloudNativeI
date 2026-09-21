package com.digitalfix_bff.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    // Bean para interceptar peticiones salientes de Feign y pasar el token JWT al
    // siguiente microservicio
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // Obtenemos los atributos de la petición
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                // Obtenemos la petición
                HttpServletRequest request = attributes.getRequest();
                // Obtenemos el header de autorización
                String authHeader = request.getHeader("Authorization");

                // Si el header existe y empieza con "Bearer ", lo añadimos al RequestTemplate
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    template.header("Authorization", authHeader);
                }
            }
        };
    }
}
