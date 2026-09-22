package com.digitalfix_bff.dto;
import java.time.LocalDateTime;

public record OrdenTrabajoDTO(
        Integer idOrdenTrabajo,
        String clienteId,
        Integer servicioId,
        Integer repuestoId,
        EstadoOrden estado,
        String direccion,
        String tecnicoAsignadoId,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {}
