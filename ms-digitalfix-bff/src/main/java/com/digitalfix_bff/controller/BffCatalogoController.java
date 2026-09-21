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
@RequestMapping("/api/bff/catalog")
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

    @GetMapping("/spares")
    public List<RepuestoDTO> listarRepuestos() {
        return catalogoClient.listarRepuestos();
    }

    @PutMapping("/spares/{id}/stock")
    public RepuestoDTO actualizarStock(@PathVariable("id") Long id, @RequestBody ActualizarStockDTO dto) {
        return catalogoClient.actualizarStock(id, dto);
    }
}
