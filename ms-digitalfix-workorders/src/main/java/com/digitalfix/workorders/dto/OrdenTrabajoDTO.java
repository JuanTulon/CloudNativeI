package com.digitalfix.workorders.dto;

import com.digitalfix.workorders.model.EstadoOrden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO principal de respuesta. Expone los datos de la Orden de Trabajo hacia el cliente.
 * Separa la capa de presentación (API) del modelo de persistencia (Entidad JPA).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO con la información completa de una orden de trabajo")
public class OrdenTrabajoDTO {
    @Schema(description = "ID único de la orden", example = "1")
    private Integer idOrdenTrabajo;
    @Schema(description = "ID del cliente", example = "CLI-10023")
    private String clienteId;
    @Schema(description = "ID del servicio", example = "5")
    private Integer servicioId;
    @Schema(description = "ID del repuesto (opcional)", example = "20")
    private Integer repuestoId;
    @Schema(description = "Estado actual de la orden", example = "CREADA")
    private EstadoOrden estado;
    @Schema(description = "Dirección del servicio", example = "Av. Siempre Viva 742")
    private String direccion;
    @Schema(description = "ID del técnico asignado", example = "TEC-99")
    private String tecnicoAsignadoId;
    @Schema(description = "Fecha y hora de creación de la orden")
    private LocalDateTime fechaCreacion;
    @Schema(description = "Fecha y hora de la última actualización")
    private LocalDateTime fechaActualizacion;
}
