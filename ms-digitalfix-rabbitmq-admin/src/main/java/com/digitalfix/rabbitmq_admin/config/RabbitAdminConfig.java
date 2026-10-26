package com.digitalfix.rabbitmq_admin.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitAdminConfig {

    /**
     *
     * auto-startup = true: RabbitAdmin inicializa la conexion al arrancar
     * y declara los beans de tipo Declarable que encuentre en el contexto.
     * En este microservicio no hay beans de ese tipo declarados, ya que la
     * gestion es 100% dinamica via REST.
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

}
