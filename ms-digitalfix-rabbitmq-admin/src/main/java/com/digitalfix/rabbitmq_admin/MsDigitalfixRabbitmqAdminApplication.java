package com.digitalfix.rabbitmq_admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio administrador de RabbitMQ.
 *
 * Responsabilidades:
 *  - Crear y eliminar colas, exchanges y bindings de forma dinamica via API REST.
 *  - Delegar en RabbitAdmin de Spring AMQP (operaciones idempotentes sobre el broker).
 *  - Exponer endpoints REST que el BFF (ms-digitalfix-bff) proxea hacia el frontend.
 *
 * Puerto: 8086.
 */
@SpringBootApplication
public class MsDigitalfixRabbitmqAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsDigitalfixRabbitmqAdminApplication.class, args);
    }
}
