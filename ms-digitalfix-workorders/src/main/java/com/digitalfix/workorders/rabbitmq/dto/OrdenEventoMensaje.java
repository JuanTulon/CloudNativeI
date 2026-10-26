package com.digitalfix.workorders.rabbitmq.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Envelope comun para todos los eventos de orden de trabajo que viajan por RabbitMQ.
 *
 * Buenas practicas aplicadas:
 *  - eventId (UUID): garantiza idempotencia en el consumidor (puede ignorar duplicados).
 *  - traceId / correlationId: trazabilidad end-to-end entre microservicios.
 *  - type: permite que el mismo consumer distinga tipos de evento sin crear multiples colas.
 *  - Constructor vacio obligatorio: Jackson lo necesita para deserializar al consumir.
 */
public class OrdenEventoMensaje implements Serializable {

    // ------------------------------------------------------------------
    // Metadatos del envelope
    // ------------------------------------------------------------------

    /**
     * Tipo de evento: "NOTIFICACION_EMAIL" | "TICKET_DESPACHO" | "GENERAR_REPORTE"
     */
    private String type;

    /**
     * UUID unico por mensaje. El consumidor puede usar esto para idempotencia:
     * si ya proceso este eventId, descarta el duplicado.
     */
    private String eventId;

    /** Timestamp de cuando se publico el evento. */
    private LocalDateTime timestamp;

    /**
     * Identificador de traza distribuida (propagado desde el header X-B3-TraceId
     * del request HTTP original). Permite correlacionar logs entre servicios.
     */
    private String traceId;

    /**
     * ID de la orden de trabajo como string. Sirve para agrupar todos los
     * eventos de una misma orden al auditar o depurar.
     */
    private String correlationId;

    // ------------------------------------------------------------------
    // Payload de la orden
    // ------------------------------------------------------------------

    private Integer idOrdenTrabajo;
    private String  clienteId;
    private String  tecnicoAsignadoId;

    /** Estado nuevo al que transiciono la orden. */
    private String  estadoNuevo;

    /** Estado anterior de la orden (util para auditar la transicion). */
    private String  estadoAnterior;

    private String  direccion;

    // ------------------------------------------------------------------
    // Constructor vacio requerido por Jackson para deserializar
    // ------------------------------------------------------------------
    public OrdenEventoMensaje() {}

    // ------------------------------------------------------------------
    // Getters y Setters
    // ------------------------------------------------------------------

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public Integer getIdOrdenTrabajo() { return idOrdenTrabajo; }
    public void setIdOrdenTrabajo(Integer idOrdenTrabajo) { this.idOrdenTrabajo = idOrdenTrabajo; }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getTecnicoAsignadoId() { return tecnicoAsignadoId; }
    public void setTecnicoAsignadoId(String tecnicoAsignadoId) { this.tecnicoAsignadoId = tecnicoAsignadoId; }

    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
