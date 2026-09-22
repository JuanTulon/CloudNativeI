package com.digitalfix_bff.controller;

// Controller que expone los métodos que se van a comunicar con el microservicio de ordenes de trabajo
// Usa el feignClient para comunicarse con el microservicio de ordenes de trabajo
import com.digitalfix_bff.client.OrdenTrabajoClient;
import com.digitalfix_bff.dto.ActualizarEstadoDTO;
import com.digitalfix_bff.dto.OrdenTrabajoCrearDTO;
import com.digitalfix_bff.dto.OrdenTrabajoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// Path base para el BFF de ordenes de trabajo
@RequestMapping({"/api/bff/workorders", "/api/workorders"})
@RequiredArgsConstructor
public class BffOrdenTrabajoController {

    private final OrdenTrabajoClient ordenTrabajoClient;

    @PostMapping
    public OrdenTrabajoDTO crearOrden(@RequestBody OrdenTrabajoCrearDTO dto) {
        return ordenTrabajoClient.crearOrden(dto);
    }

    @GetMapping("/{id}")
    public OrdenTrabajoDTO obtenerOrdenPorId(@PathVariable("id") Long id) {
        return ordenTrabajoClient.obtenerOrdenPorId(id);
    }

    @PutMapping("/{id}/status")
    public OrdenTrabajoDTO actualizarEstado(@PathVariable("id") Long id, @RequestBody ActualizarEstadoDTO dto) {
        return ordenTrabajoClient.actualizarEstado(id, dto);
    }
}
