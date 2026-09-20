package com.digitalfix.workorders.dto;

import com.digitalfix.workorders.model.EstadoOrden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la actualización parcial (PATCH) enfocado únicamente en cambiar el estado de la Orden.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para actualizar el estado de una orden")
public class ActualizarEstadoDTO {
    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Nuevo estado de la orden", example = "EN_EJECUCION")
    private EstadoOrden estado;
}
