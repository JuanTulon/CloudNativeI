package com.digitalfix.catalogo.dto;

import com.digitalfix.catalogo.model.Servicio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Información de un servicio técnico")
public class ServicioDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private BigDecimal tarifa;
    private Boolean activo;

    public static ServicioDTO from(Servicio s) {
        return ServicioDTO.builder().id(s.getId()).nombre(s.getNombre()).descripcion(s.getDescripcion())
                .tarifa(s.getTarifa()).activo(s.getActivo()).build();
    }
}
