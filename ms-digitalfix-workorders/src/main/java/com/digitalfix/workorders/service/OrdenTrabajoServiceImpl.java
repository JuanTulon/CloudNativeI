package com.digitalfix.workorders.service;

import com.digitalfix.workorders.dto.ActualizarEstadoDTO;
import com.digitalfix.workorders.dto.OrdenTrabajoCrearDTO;
import com.digitalfix.workorders.dto.OrdenTrabajoDTO;
import com.digitalfix.workorders.exception.BusinessRuleException;
import com.digitalfix.workorders.exception.ResourceNotFoundException;
import com.digitalfix.workorders.model.EstadoOrden;
import com.digitalfix.workorders.model.OrdenTrabajo;
import com.digitalfix.workorders.rabbitmq.config.RabbitMQConfig;
import com.digitalfix.workorders.rabbitmq.dto.OrdenEventoMensaje;
import com.digitalfix.workorders.rabbitmq.producer.OrdenEventoProducer;
import com.digitalfix.workorders.repository.OrdenTrabajoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {

    private static final Logger log = LoggerFactory.getLogger(OrdenTrabajoServiceImpl.class);

    private final OrdenTrabajoRepository ordenTrabajoRepository;

    /**
     * Producer RabbitMQ inyectado para publicar eventos de forma asincrona.
     * Al llamar a publicar(), el servicio responde inmediatamente al BFF
     * y el procesamiento (email, despacho, reporte) ocurre en segundo plano.
     */
    private final OrdenEventoProducer ordenEventoProducer;

    @Override
    public OrdenTrabajoDTO crearOrden(OrdenTrabajoCrearDTO dto) {
        // toma los datos del dto y crea una nueva orden
        OrdenTrabajo orden = OrdenTrabajo.builder()
                .clienteId(dto.getClienteId())
                .servicioId(dto.getServicioId())
                .repuestoId(dto.getRepuestoId())
                .direccion(dto.getDireccion())
                .estado(EstadoOrden.CREADA)
                .build();

        OrdenTrabajo saved = ordenTrabajoRepository.save(orden);
        return mapToDTO(saved);
    }

    @Override
    public OrdenTrabajoDTO obtenerOrdenPorId(Integer id) {
        OrdenTrabajo orden = ordenTrabajoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada"));
        return mapToDTO(orden);
    }

    @Override
    public java.util.List<OrdenTrabajoDTO> obtenerTodas() {
        return ordenTrabajoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public OrdenTrabajoDTO actualizarEstado(Integer id, ActualizarEstadoDTO dto) {
        OrdenTrabajo orden = ordenTrabajoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada"));

        // verifica obligatoriamente que el estado actual de la entidad sea
        // EstadoOrden.ASIGNADA. Si no lo es, lanza un ReglaNegocioException
        if (dto.getEstado() == EstadoOrden.EN_EJECUCION && orden.getEstado() != EstadoOrden.ASIGNADA) {
            throw new BusinessRuleException("No se puede pasar a EN_EJECUCION sin haber sido ASIGNADA previamente");
        }

        EstadoOrden estadoAnterior = orden.getEstado();
        orden.setEstado(dto.getEstado());
        OrdenTrabajo updated = ordenTrabajoRepository.save(orden);

        // -----------------------------------------------------------------
        // Publicacion asincrona de eventos segun la nueva transicion de estado.
        // El servicio responde al BFF de inmediato; los consumidores (notify,
        // report, etc.) procesan en sus propios hilos sin bloquear este request.
        // -----------------------------------------------------------------
        publicarEventosPorEstado(updated, estadoAnterior);

        return mapToDTO(updated);
    }

    /**
     * Decide que eventos publicar segun el estado nuevo de la orden.
     *
     * Reglas del caso:
     *  ASIGNADA          → email al cliente + ticket de despacho al tecnico
     *  EN_DESPLAZAMIENTO → email al cliente (tecnico en camino)
     *  CERRADA           → email al cliente (trabajo cerrado) + generar reporte PDF
     *  CANCELADA         → email al cliente (orden cancelada)
     */
    private void publicarEventosPorEstado(OrdenTrabajo orden, EstadoOrden estadoAnterior) {
        switch (orden.getEstado()) {
            case ASIGNADA -> {
                // Notifica al cliente por email
                ordenEventoProducer.publicar(
                        RabbitMQConfig.RK_EMAIL_SEND,
                        buildEvento("NOTIFICACION_EMAIL", orden, estadoAnterior));

                // Genera ticket de despacho para el tecnico
                ordenEventoProducer.publicar(
                        RabbitMQConfig.RK_DISPATCH_TICKET,
                        buildEvento("TICKET_DESPACHO", orden, estadoAnterior));
            }
            case EN_DESPLAZAMIENTO -> {
                // Notifica al cliente que el tecnico ya va en camino
                ordenEventoProducer.publicar(
                        RabbitMQConfig.RK_EMAIL_SEND,
                        buildEvento("NOTIFICACION_EMAIL", orden, estadoAnterior));
            }
            case CERRADA -> {
                // Notifica al cliente que el trabajo fue completado
                ordenEventoProducer.publicar(
                        RabbitMQConfig.RK_EMAIL_SEND,
                        buildEvento("NOTIFICACION_EMAIL", orden, estadoAnterior));

                // Solicita generacion del informe tecnico / certificado de trabajo
                ordenEventoProducer.publicar(
                        RabbitMQConfig.RK_REPORT_GEN,
                        buildEvento("GENERAR_REPORTE", orden, estadoAnterior));
            }
            case CANCELADA -> {
                // Notifica al cliente que la orden fue cancelada
                ordenEventoProducer.publicar(
                        RabbitMQConfig.RK_EMAIL_SEND,
                        buildEvento("NOTIFICACION_EMAIL", orden, estadoAnterior));
            }
            default -> log.debug("[SERVICE] Estado {} no genera eventos RabbitMQ", orden.getEstado());
        }
    }

    /**
     * Construye el envelope comun con todos los metadatos necesarios.
     *
     * @param type          tipo de evento ("NOTIFICACION_EMAIL", "TICKET_DESPACHO", "GENERAR_REPORTE")
     * @param orden         entidad actualizada con el nuevo estado
     * @param estadoAnterior estado previo a la transicion
     */
    private OrdenEventoMensaje buildEvento(String type, OrdenTrabajo orden, EstadoOrden estadoAnterior) {
        OrdenEventoMensaje evento = new OrdenEventoMensaje();
        evento.setType(type);
        evento.setEventId(UUID.randomUUID().toString());
        evento.setTimestamp(LocalDateTime.now());
        // correlationId = idOrdenTrabajo como String (agrupa todos los eventos de una orden)
        evento.setCorrelationId(String.valueOf(orden.getIdOrdenTrabajo()));
        // traceId: en produccion se propaga desde el header HTTP (MDC/X-B3-TraceId)
        evento.setTraceId(UUID.randomUUID().toString());
        evento.setIdOrdenTrabajo(orden.getIdOrdenTrabajo());
        evento.setClienteId(orden.getClienteId());
        evento.setTecnicoAsignadoId(orden.getTecnicoAsignadoId());
        evento.setEstadoNuevo(orden.getEstado().name());
        evento.setEstadoAnterior(estadoAnterior.name());
        evento.setDireccion(orden.getDireccion());
        return evento;
    }

    private OrdenTrabajoDTO mapToDTO(OrdenTrabajo orden) {
        return OrdenTrabajoDTO.builder()
                .idOrdenTrabajo(orden.getIdOrdenTrabajo())
                .clienteId(orden.getClienteId())
                .servicioId(orden.getServicioId())
                .repuestoId(orden.getRepuestoId())
                .estado(orden.getEstado())
                .direccion(orden.getDireccion())
                .tecnicoAsignadoId(orden.getTecnicoAsignadoId())
                .fechaCreacion(orden.getFechaCreacion())
                .fechaActualizacion(orden.getFechaActualizacion())
                .build();
    }
}
