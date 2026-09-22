package com.digitalfix_bff.dto;
import java.math.BigDecimal;

public record ServicioDTO(
        Integer id,
        String nombre,
        String descripcion,
        BigDecimal tarifa,
        Boolean activo
) {}
