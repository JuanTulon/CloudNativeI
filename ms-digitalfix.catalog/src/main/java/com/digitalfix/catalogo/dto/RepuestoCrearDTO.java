package com.digitalfix.catalogo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RepuestoCrearDTO {
    @NotBlank private String codigo;
    @NotBlank private String nombre;
    private String descripcion;
    @NotNull @Min(0) private Integer stock;
    @NotNull @DecimalMin("0.0") private BigDecimal precio;
}
