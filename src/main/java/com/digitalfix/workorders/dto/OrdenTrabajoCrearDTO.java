package com.digitalfix.workorders.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado exclusivamente para recibir los datos de creación (POST) de una nueva Orden de Trabajo.
 * Sus validaciones (@NotNull, @NotBlank) garantizan que no lleguen datos incompletos al servicio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para la creación de una nueva orden de trabajo")
public class OrdenTrabajoCrearDTO {

    @NotBlank(message = "El clienteId es obligatorio")
    @Schema(description = "ID del cliente que solicita la orden", example = "CLI-10023")
    private String clienteId;

    @NotNull(message = "El servicioId es obligatorio")
    @Schema(description = "ID del servicio a realizar", example = "5")
    private Integer servicioId;

    @Schema(description = "ID del repuesto necesario (opcional)", example = "20")
    private Integer repuestoId;

    @NotBlank(message = "La direccion es obligatoria")
    @Schema(description = "Dirección donde se realizará el servicio", example = "Av. Siempre Viva 742")
    private String direccion;
}
