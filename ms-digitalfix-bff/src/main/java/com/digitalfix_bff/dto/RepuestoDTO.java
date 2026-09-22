package com.digitalfix_bff.dto;
import java.math.BigDecimal;

public record RepuestoDTO(
        Integer id,
        String codigo,
        String nombre,
        String descripcion,
        Integer stock,
        BigDecimal precio,
        Boolean activo
) {}
