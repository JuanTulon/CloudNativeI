package com.digitalfix.workorders.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Interceptador global de excepciones para los controladores REST.
 * Convierte las excepciones lanzadas en cualquier parte de la aplicación 
 * en respuestas HTTP estructuradas y consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de lógica de negocio (ej. estados inválidos o acciones no permitidas).
     * Retorna un HTTP 400 (Bad Request).
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorApi> handleBusinessRuleException(BusinessRuleException ex, HttpServletRequest request) {
        ErrorApi error = ErrorApi.builder()
                .fechaHora(LocalDateTime.now())
                .estadoHttp(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .mensaje(ex.getMessage())
                .ruta(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores cuando se intenta buscar o manipular un registro que no existe (ej. ID inexistente).
     * Retorna un HTTP 404 (Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorApi> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorApi error = ErrorApi.builder()
                .fechaHora(LocalDateTime.now())
                .estadoHttp(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .mensaje(ex.getMessage())
                .ruta(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Captura los errores de validación arrojados por las anotaciones de los DTOs (@NotNull, @NotBlank, etc).
     * Retorna un HTTP 400 (Bad Request) concatenando los mensajes de cada campo que haya fallado.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensajesValidacion = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorApi error = ErrorApi.builder()
                .fechaHora(LocalDateTime.now())
                .estadoHttp(HttpStatus.BAD_REQUEST.value())
                .error("Error de Validación")
                .mensaje(mensajesValidacion)
                .ruta(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
