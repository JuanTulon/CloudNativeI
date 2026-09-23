package com.digitalfix_bff.client;

import com.digitalfix_bff.config.FeignClientConfig;
import com.digitalfix_bff.dto.ActualizarStockDTO;
import com.digitalfix_bff.dto.RepuestoDTO;
import com.digitalfix_bff.dto.ServicioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "catalogo-service", url = "${CATALOG_URL:http://localhost:8082/api/catalog}", configuration = FeignClientConfig.class)
public interface CatalogoClient {

    @GetMapping("/services")
    List<ServicioDTO> listarServicios();

    @PostMapping("/services")
    ServicioDTO crearServicio(@RequestBody ServicioDTO dto);

    @GetMapping("/parts")
    List<RepuestoDTO> listarRepuestos();

    @PostMapping("/parts")
    RepuestoDTO crearRepuesto(@RequestBody RepuestoDTO dto);

    @GetMapping("/parts/{id}")
    RepuestoDTO obtenerRepuesto(@PathVariable("id") Long id);

    @PutMapping("/parts/{id}")
    RepuestoDTO actualizarStock(@PathVariable("id") Long id, @RequestBody RepuestoDTO dto);
}
