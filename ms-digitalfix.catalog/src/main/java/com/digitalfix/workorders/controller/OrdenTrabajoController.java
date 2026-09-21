package com.digitalfix.workorders.controller;

import com.digitalfix.workorders.dto.ActualizarEstadoDTO;
import com.digitalfix.workorders.dto.OrdenTrabajoCrearDTO;
import com.digitalfix.workorders.dto.OrdenTrabajoDTO;
import com.digitalfix.workorders.service.OrdenTrabajoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.digitalfix.workorders.exception.ErrorApi;

@RestController
@RequestMapping("/api/workorders")
@Tag(name = "Órdenes de Trabajo", description = "Endpoints para la gestión del ciclo de vida de las órdenes")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenTrabajoService;

    @Operation(summary = "Crear orden de trabajo", description = "Crea una nueva orden de trabajo con los datos iniciales y estado CREADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenTrabajoDTO.class),
                            examples = @ExampleObject(name = "Orden Creada", value = """
                                    {
                                        "idOrdenTrabajo": 1,
                                        "clienteId": "CLI-10023",
                                        "servicioId": 5,
                                        "repuestoId": 20,
                                        "estado": "CREADA",
                                        "direccion": "Av. Siempre Viva 742",
                                        "tecnicoAsignadoId": null,
                                        "fechaCreacion": "2023-10-27T10:00:00",
                                        "fechaActualizacion": "2023-10-27T10:00:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Error de validación o regla de negocio", content = @Content(schema = @Schema(implementation = ErrorApi.class)))
    })
    @PostMapping
    public ResponseEntity<OrdenTrabajoDTO> crearOrden(@Valid @RequestBody OrdenTrabajoCrearDTO dto) {
        OrdenTrabajoDTO creada = ordenTrabajoService.crearOrden(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Buscar orden por ID", description = "Retorna los detalles completos de una orden de trabajo si existe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenTrabajoDTO.class),
                            examples = @ExampleObject(name = "Orden Encontrada", value = """
                                    {
                                        "idOrdenTrabajo": 1,
                                        "clienteId": "CLI-10023",
                                        "servicioId": 5,
                                        "repuestoId": 20,
                                        "estado": "ASIGNADA",
                                        "direccion": "Av. Siempre Viva 742",
                                        "tecnicoAsignadoId": "TEC-99",
                                        "fechaCreacion": "2023-10-27T10:00:00",
                                        "fechaActualizacion": "2023-10-27T11:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content(schema = @Schema(implementation = ErrorApi.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajoDTO> obtenerOrdenPorId(@PathVariable Integer id) {
        OrdenTrabajoDTO orden = ordenTrabajoService.obtenerOrdenPorId(id);
        return ResponseEntity.ok(orden);
    }

    @Operation(summary = "Actualizar estado de la orden", description = "Cambia el estado de una orden. Regla: no se puede pasar a EN_EJECUCION sin antes estar ASIGNADA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdenTrabajoDTO.class),
                            examples = @ExampleObject(name = "Estado Actualizado", value = """
                                    {
                                        "idOrdenTrabajo": 1,
                                        "clienteId": "CLI-10023",
                                        "servicioId": 5,
                                        "repuestoId": 20,
                                        "estado": "EN_EJECUCION",
                                        "direccion": "Av. Siempre Viva 742",
                                        "tecnicoAsignadoId": "TEC-99",
                                        "fechaCreacion": "2023-10-27T10:00:00",
                                        "fechaActualizacion": "2023-10-27T14:15:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Regla de negocio violada", content = @Content(schema = @Schema(implementation = ErrorApi.class))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content(schema = @Schema(implementation = ErrorApi.class)))
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrdenTrabajoDTO> actualizarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstadoDTO dto) {
        OrdenTrabajoDTO actualizada = ordenTrabajoService.actualizarEstado(id, dto);
        return ResponseEntity.ok(actualizada);
    }
}
