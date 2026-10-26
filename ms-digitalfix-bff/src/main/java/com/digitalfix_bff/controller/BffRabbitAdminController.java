package com.digitalfix_bff.controller;

import com.digitalfix_bff.client.RabbitAdminClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller BFF que expone la gestion de RabbitMQ hacia el frontend.
 *
 * Actua como proxy seguro: valida el JWT de Azure AD antes de reenviar
 * cada llamada al ms-digitalfix-rabbitmq-admin (puerto 8086).
 *
 * Solo el rol Admin puede operar sobre la infraestructura del broker.
 *
 * Prefijo base accesible desde el frontend: /api/rabbit
 */
@RestController
@RequestMapping("/api/rabbit")
@RequiredArgsConstructor
public class BffRabbitAdminController {

    private final RabbitAdminClient rabbitAdminClient;

    // COLAS

    /** Lista todas las colas conocidas con propiedades del broker. */
    @GetMapping("/queues")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<Map<String, Object>>> listarColas() {
        return ResponseEntity.ok(rabbitAdminClient.listarColas());
    }

    /** Crea una nueva cola en el broker. */
    @PostMapping("/queues")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, String>> crearCola(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(rabbitAdminClient.crearCola(dto));
    }

    /** Elimina una cola del broker por nombre. */
    @DeleteMapping("/queues/{name}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, String>> eliminarCola(@PathVariable String name) {
        return ResponseEntity.ok(rabbitAdminClient.eliminarCola(name));
    }

    // EXCHANGES
    /** Lista todos los exchanges conocidos del sistema. */
    @GetMapping("/exchanges")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<Map<String, Object>>> listarExchanges() {
        return ResponseEntity.ok(rabbitAdminClient.listarExchanges());
    }

    /** Crea un exchange en el broker (direct, topic, fanout o headers). */
    @PostMapping("/exchanges")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, String>> crearExchange(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(rabbitAdminClient.crearExchange(dto));
    }

    /** Elimina un exchange del broker por nombre. */
    @DeleteMapping("/exchanges/{name}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, String>> eliminarExchange(@PathVariable String name) {
        return ResponseEntity.ok(rabbitAdminClient.eliminarExchange(name));
    }


    // BINDINGS
    /** Lista todos los bindings conocidos del sistema. */
    @GetMapping("/bindings")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<List<Map<String, Object>>> listarBindings() {
        return ResponseEntity.ok(rabbitAdminClient.listarBindings());
    }

    /** Crea un binding entre un exchange y una cola. */
    @PostMapping("/bindings")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, String>> crearBinding(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(rabbitAdminClient.crearBinding(dto));
    }

    /** Elimina un binding del broker (identificado por queue + exchange + routingKey). */
    @DeleteMapping("/bindings")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Map<String, String>> eliminarBinding(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(rabbitAdminClient.eliminarBinding(dto));
    }
}
