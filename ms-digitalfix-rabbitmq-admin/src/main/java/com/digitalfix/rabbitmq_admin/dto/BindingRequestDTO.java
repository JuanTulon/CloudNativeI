package com.digitalfix.rabbitmq_admin.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para crear o eliminar un binding entre un exchange y una cola.
 *
 * Ejemplo de body JSON:
 * {
 *   "queue": "q.mi.cola",
 *   "exchange": "mi.exchange",
 *   "routingKey": "mi.routing.key"
 * }
 *
 * Un binding es la "regla de enrutamiento" que conecta un exchange con una cola.
 * Sin binding, los mensajes publicados al exchange se pierden silenciosamente.
 */
public class BindingRequestDTO {

    /** Nombre de la cola destino. Requerido. */
    @NotBlank(message = "El nombre de la cola es obligatorio")
    private String queue;

    /** Nombre del exchange origen. Requerido. */
    @NotBlank(message = "El nombre del exchange es obligatorio")
    private String exchange;

    /** Routing key usada para filtrar los mensajes. Requerido. */
    @NotBlank(message = "La routing key es obligatoria")
    private String routingKey;

    // Getters y Setters

    public String getQueue() { return queue; }
    public void setQueue(String queue) { this.queue = queue; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
}
