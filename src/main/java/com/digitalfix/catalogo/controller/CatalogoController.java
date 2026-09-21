package com.digitalfix.catalogo.controller;

import com.digitalfix.catalogo.dto.*;
import com.digitalfix.catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Servicios técnicos, repuestos, stock y tarifas")
@SecurityRequirement(name = "bearerAuth")
public class CatalogoController {
    private final CatalogoService catalogoService;

    @Operation(summary = "Listar servicios técnicos")
    @GetMapping("/services")
    public ResponseEntity<List<ServicioDTO>> listarServicios() { return ResponseEntity.ok(catalogoService.listarServicios()); }

    @Operation(summary = "Obtener servicio técnico")
    @GetMapping("/services/{id}")
    public ResponseEntity<ServicioDTO> obtenerServicio(@PathVariable Integer id) { return ResponseEntity.ok(catalogoService.obtenerServicio(id)); }

    @Operation(summary = "Crear servicio técnico")
    @PostMapping("/services")
    public ResponseEntity<ServicioDTO> crearServicio(@Valid @RequestBody ServicioCrearDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearServicio(dto)); }

    @Operation(summary = "Actualizar servicio, tarifa o estado")
    @PutMapping("/services/{id}")
    public ResponseEntity<ServicioDTO> actualizarServicio(@PathVariable Integer id, @Valid @RequestBody ServicioActualizarDTO dto) { return ResponseEntity.ok(catalogoService.actualizarServicio(id, dto)); }

    @Operation(summary = "Listar repuestos y stock")
    @GetMapping("/parts")
    public ResponseEntity<List<RepuestoDTO>> listarRepuestos() { return ResponseEntity.ok(catalogoService.listarRepuestos()); }

    @Operation(summary = "Obtener repuesto")
    @GetMapping("/parts/{id}")
    public ResponseEntity<RepuestoDTO> obtenerRepuesto(@PathVariable Integer id) { return ResponseEntity.ok(catalogoService.obtenerRepuesto(id)); }

    @Operation(summary = "Crear repuesto")
    @PostMapping("/parts")
    public ResponseEntity<RepuestoDTO> crearRepuesto(@Valid @RequestBody RepuestoCrearDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearRepuesto(dto)); }

    @Operation(summary = "Actualizar repuesto y stock")
    @PutMapping("/parts/{id}")
    public ResponseEntity<RepuestoDTO> actualizarRepuesto(@PathVariable Integer id, @Valid @RequestBody RepuestoActualizarDTO dto) { return ResponseEntity.ok(catalogoService.actualizarRepuesto(id, dto)); }
}
