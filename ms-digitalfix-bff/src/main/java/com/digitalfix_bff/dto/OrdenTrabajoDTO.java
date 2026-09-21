package com.digitalfix_bff.dto;

import java.time.LocalDateTime;

public record OrdenTrabajoDTO(
        Long id,
        String titulo,
        String descripcion,
        EstadoOrden estado,
        Long tecnicoId,
        Long clienteId,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
}
