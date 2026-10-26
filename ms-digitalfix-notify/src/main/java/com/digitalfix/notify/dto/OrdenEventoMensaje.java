package com.digitalfix.notify.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Envelope comun que llega desde ms-digitalfix-workorders via RabbitMQ.
 *
 * Debe ser identico (mismos campos) al OrdenEventoMensaje del producer.
 * Jackson usa el constructor vacio y los setters para deserializar;
 * sin el constructor vacio el consumidor falla al recibir mensajes.
 */
public class OrdenEventoMensaje implements Serializable {

    private String type;
    private String eventId;
    private LocalDateTime timestamp;
    private String traceId;
    private String correlationId;
    private Integer idOrdenTrabajo;
    private String  clienteId;
    private String  tecnicoAsignadoId;
    private String  estadoNuevo;
    private String  estadoAnterior;
    private String  direccion;

    // Constructor vacio requerido por Jackson
    public OrdenEventoMensaje() {}

    // Getters y Setters

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
