package com.digitalfix.rabbitmq_admin.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear o identificar una cola en RabbitMQ.
 *
 * Ejemplo de body JSON:
 * {
 *   "name": "q.mi.cola",
 *   "durable": true,
 *   "deadLetterExchange": "cmd.dead.dlx",
 *   "deadLetterRoutingKey": "mi.cola.dlq"
 * }
 */
public class QueueRequestDTO {

    /** Nombre de la cola. Requerido. */
    @NotBlank(message = "El nombre de la cola es obligatorio")
    private String name;

    /**
     * Si true, la cola sobrevive al reinicio del broker.
     * Por defecto true.
     */
    private boolean durable = true;

    /**
     * Exchange dead-letter opcional. Si se provee, los mensajes NACK sin requeue
     * van a este exchange automaticamente.
     */
    private String deadLetterExchange;

    /**
     * Routing key para el dead-letter exchange. Solo tiene efecto si
     * deadLetterExchange esta configurado.
     */
    private String deadLetterRoutingKey;

    // Getters y Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }

    public String getDeadLetterExchange() { return deadLetterExchange; }
    public void setDeadLetterExchange(String deadLetterExchange) { this.deadLetterExchange = deadLetterExchange; }

    public String getDeadLetterRoutingKey() { return deadLetterRoutingKey; }
    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) { this.deadLetterRoutingKey = deadLetterRoutingKey; }
}
