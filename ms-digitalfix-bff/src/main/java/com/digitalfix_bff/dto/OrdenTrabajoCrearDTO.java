package com.digitalfix_bff.dto;

public record OrdenTrabajoCrearDTO(
        String titulo,
        String descripcion,
        Long clienteId
) {
}
