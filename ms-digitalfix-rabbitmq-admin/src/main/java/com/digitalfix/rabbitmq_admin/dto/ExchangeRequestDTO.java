package com.digitalfix.rabbitmq_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para crear o identificar un exchange en RabbitMQ.
 *
 * Ejemplo de body JSON:
 * {
 *   "name": "mi.exchange",
 *   "type": "direct",
 *   "durable": true
 * }
 */
public class ExchangeRequestDTO {

    /** Nombre del exchange. Requerido. */
    @NotBlank(message = "El nombre del exchange es obligatorio")
    private String name;

    /**
     * Tipo del exchange. Valores validos: direct, topic, fanout, headers.
     * Por defecto: direct.
     *
     * - direct:  enruta por routing key exacta.
     * - topic:   enruta por patron (*.word, word.#).
     * - fanout:  enruta a todas las colas vinculadas (broadcast).
     * - headers: enruta por cabeceras AMQP (raramente usado).
     */
    @Pattern(
        regexp = "direct|topic|fanout|headers",
        message = "Tipo de exchange invalido. Use: direct, topic, fanout o headers"
    )
    private String type = "direct";

    /**
     * Si true, el exchange sobrevive al reinicio del broker.
     */
    private boolean durable = true;

    // Getters y Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }
}
