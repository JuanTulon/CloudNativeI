package com.digitalfix_bff.dto;

public record RepuestoDTO(
        Long id,
        String nombre,
        Integer stock,
        Double precio
) {
}
