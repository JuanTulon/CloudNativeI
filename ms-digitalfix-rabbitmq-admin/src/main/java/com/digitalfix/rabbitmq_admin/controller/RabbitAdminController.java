package com.digitalfix.rabbitmq_admin.controller;

import com.digitalfix.rabbitmq_admin.dto.BindingRequestDTO;
import com.digitalfix.rabbitmq_admin.dto.ExchangeRequestDTO;
import com.digitalfix.rabbitmq_admin.dto.QueueRequestDTO;
import com.digitalfix.rabbitmq_admin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST del microservicio administrador de RabbitMQ.
 *
 * Expone endpoints para gestionar de forma dinamica la infraestructura
 * del broker: colas, exchanges y bindings.
 *
 * El BFF  actua como proxy de estos endpoints,
 * aplicando autenticacion JWT antes de reenviar las llamadas.
 *
 */
@RestController
@RequestMapping("/rabbit")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    // COLAS
    /**
     * Lista todas las colas conocidas con sus propiedades del broker
     * (cantidad de mensajes, consumidores activos).
     */
    @GetMapping("/queues")
    public ResponseEntity<List<Map<String, Object>>> listarColas() {
        return ResponseEntity.ok(rabbitAdminService.listarColas());
    }

    /**
     * Crea una nueva cola en RabbitMQ.
     * Si la cola ya existe con los mismos parametros, la operacion es idempotente.
     *
     */
    @PostMapping("/queues")
    public ResponseEntity<Map<String, String>> crearCola(@Valid @RequestBody QueueRequestDTO dto) {
        return ResponseEntity.ok(rabbitAdminService.crearCola(dto));
    }

    /**
     * Elimina una cola del broker por nombre.
     * Si la cola no existe, retorna un mensaje informativo sin error.
     *

     */
    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Map<String, String>> eliminarCola(@PathVariable String name) {
        return ResponseEntity.ok(rabbitAdminService.eliminarCola(name));
    }

    // EXCHANGES

    /**
     * Lista todos los exchanges conocidos del sistema.
     */
    @GetMapping("/exchanges")
    public ResponseEntity<List<Map<String, Object>>> listarExchanges() {
        return ResponseEntity.ok(rabbitAdminService.listarExchanges());
    }

    /**
     * Crea un exchange en RabbitMQ.
     * Tipos validos: direct, topic, fanout, headers.
     */
    @PostMapping("/exchanges")
    public ResponseEntity<Map<String, String>> crearExchange(@Valid @RequestBody ExchangeRequestDTO dto) {
        return ResponseEntity.ok(rabbitAdminService.crearExchange(dto));
    }

    /**
     * Elimina un exchange del broker por nombre.
     * Falla si el exchange tiene colas vinculadas activas.
     */
    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Map<String, String>> eliminarExchange(@PathVariable String name) {
        return ResponseEntity.ok(rabbitAdminService.eliminarExchange(name));
    }

    // BINDINGS
    /**
     * Lista todos los bindings conocidos del sistema.
     */
    @GetMapping("/bindings")
    public ResponseEntity<List<BindingRequestDTO>> listarBindings() {
        return ResponseEntity.ok(rabbitAdminService.listarBindings());
    }

    /**
     * Crea un binding entre un exchange y una cola.
     *
     */
    @PostMapping("/bindings")
    public ResponseEntity<Map<String, String>> crearBinding(@Valid @RequestBody BindingRequestDTO dto) {
        return ResponseEntity.ok(rabbitAdminService.crearBinding(dto));
    }

    /**
     * Elimina un binding del broker.
     * El binding se identifica por la combinacion unica de queue + exchange + routingKey.
     *
     */
    @DeleteMapping("/bindings")
    public ResponseEntity<Map<String, String>> eliminarBinding(@Valid @RequestBody BindingRequestDTO dto) {
        return ResponseEntity.ok(rabbitAdminService.eliminarBinding(dto));
    }

    // HEALTHCHECK
    /** Endpoint de salud para Docker / load balancer. */
    @GetMapping("/salud")
    public ResponseEntity<Map<String, String>> salud() {
        return ResponseEntity.ok(Map.of(
                "servicio", "ms-digitalfix-rabbitmq-admin",
                "estado",   "operativo",
                "version",  "1.0.0"
        ));
    }
}
