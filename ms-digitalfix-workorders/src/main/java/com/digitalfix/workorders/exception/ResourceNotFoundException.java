package com.digitalfix.workorders.exception;

/**
 * Excepción cuando no se encuentra un recurso.
 * Ej: "ID inexistente".
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
