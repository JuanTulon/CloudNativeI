package com.digitalfix.catalogo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorApi> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(ex.getMessage(), HttpStatus.NOT_FOUND, req);
    }
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorApi> business(BusinessRuleException ex, HttpServletRequest req) {
        return build(ex.getMessage(), HttpStatus.BAD_REQUEST, req);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).collect(Collectors.joining(", "));
        return build(msg, HttpStatus.BAD_REQUEST, req);
    }
    private ResponseEntity<ErrorApi> build(String msg, HttpStatus status, HttpServletRequest req) {
        ErrorApi error = ErrorApi.builder().fechaHora(LocalDateTime.now()).estadoHttp(status.value())
                .error(status.getReasonPhrase()).mensaje(msg).ruta(req.getRequestURI()).build();
        return ResponseEntity.status(status).body(error);
    }
}
