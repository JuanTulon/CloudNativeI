package com.digitalfix_bff.dto;

public record ServicioDTO(
        Long id,
        String nombre,
        String descripcion,
        Double tarifa
) {
}
