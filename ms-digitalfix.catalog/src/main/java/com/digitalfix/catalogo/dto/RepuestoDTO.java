package com.digitalfix.catalogo.dto;

import com.digitalfix.catalogo.model.Repuesto;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RepuestoDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer stock;
    private BigDecimal precio;
    private Boolean activo;
    public static RepuestoDTO from(Repuesto r) {
        return RepuestoDTO.builder().id(r.getId()).codigo(r.getCodigo()).nombre(r.getNombre())
                .descripcion(r.getDescripcion()).stock(r.getStock()).precio(r.getPrecio()).activo(r.getActivo()).build();
    }
}
