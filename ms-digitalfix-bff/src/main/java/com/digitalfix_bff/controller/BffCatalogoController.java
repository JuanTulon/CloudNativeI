package com.digitalfix_bff.controller;

import com.digitalfix_bff.client.CatalogoClient;
import com.digitalfix_bff.dto.ActualizarStockDTO;
import com.digitalfix_bff.dto.RepuestoDTO;
import com.digitalfix_bff.dto.ServicioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/bff/catalog", "/api/catalog"})
@RequiredArgsConstructor
public class BffCatalogoController {

    private final CatalogoClient catalogoClient;

    @GetMapping("/services")
    public List<ServicioDTO> listarServicios() {
        return catalogoClient.listarServicios();
    }

    @PostMapping("/services")
    public ServicioDTO crearServicio(@RequestBody ServicioDTO dto) {
        return catalogoClient.crearServicio(dto);
    }

    @GetMapping({"/spares", "/parts"})
    public List<RepuestoDTO> listarRepuestos() {
        return catalogoClient.listarRepuestos();
    }

    @PostMapping({"/spares", "/parts"})
    public RepuestoDTO crearRepuesto(@RequestBody RepuestoDTO dto) {
        return catalogoClient.crearRepuesto(dto);
    }

    @PutMapping({"/spares/{id}/stock", "/parts/{id}/stock"})
    public RepuestoDTO actualizarStock(@PathVariable("id") Long id, @RequestBody ActualizarStockDTO dto) {
        RepuestoDTO existing = catalogoClient.obtenerRepuesto(id);
        if (existing == null) throw new RuntimeException("Repuesto no encontrado");
        RepuestoDTO updated = new RepuestoDTO(
            existing.id(),
            existing.codigo(),
            existing.nombre(),
            existing.descripcion(),
            dto.cantidad(),
            existing.precio(),
            existing.activo()
        );
        return catalogoClient.actualizarStock(id, updated);
    }
}
