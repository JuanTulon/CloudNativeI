package com.digitalfix_bff.dto;

public record OrdenTrabajoCrearDTO(
        String clienteId,
        Integer servicioId,
        Integer repuestoId,
        String direccion
) {}
