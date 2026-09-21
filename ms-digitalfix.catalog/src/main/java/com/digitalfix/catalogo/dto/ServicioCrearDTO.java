package com.digitalfix.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Datos para crear un servicio técnico")
public class ServicioCrearDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    @NotNull(message = "La tarifa es obligatoria") @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal tarifa;
}
