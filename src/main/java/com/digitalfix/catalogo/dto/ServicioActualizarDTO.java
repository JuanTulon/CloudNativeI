package com.digitalfix.catalogo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ServicioActualizarDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    @NotNull(message = "La tarifa es obligatoria") @DecimalMin("0.0")
    private BigDecimal tarifa;
    @NotNull(message = "El activo es obligatorio")
    private Boolean activo;
}
