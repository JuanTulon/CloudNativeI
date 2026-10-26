package com.digitalfix.notify.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint de healthcheck interno para Docker Compose / AWS health checks.
 * No es publico: solo accesible desde la red interna de contenedores.
 */
@RestController
@RequestMapping("/notify")
public class NotifyHealthController {

    /** Comprobacion de que el servicio consumidor esta vivo y conectado. */
    @GetMapping("/salud")
    public ResponseEntity<Map<String, String>> salud() {
        return ResponseEntity.ok(Map.of(
                "servicio", "ms-digitalfix-notify",
                "estado",   "operativo",
                "colas",    "q.cmd.email | q.cmd.dispatch"
        ));
    }
}
