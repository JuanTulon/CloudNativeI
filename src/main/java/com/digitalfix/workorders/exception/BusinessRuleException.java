package com.digitalfix.workorders.exception;

/**
 * Excepción para reglas de negocio.
 * Ej: "No se puede cancelar una orden que ya está en ejecución".
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
