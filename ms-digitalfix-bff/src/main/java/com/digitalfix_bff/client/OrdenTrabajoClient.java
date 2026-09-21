package com.digitalfix_bff.client;

import com.digitalfix_bff.config.FeignClientConfig;
import com.digitalfix_bff.dto.ActualizarEstadoDTO;
import com.digitalfix_bff.dto.OrdenTrabajoCrearDTO;
import com.digitalfix_bff.dto.OrdenTrabajoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Interface que define los métodos que se van a comunicar con el microservicio de ordenes de trabajo
//url del servicio de ordenes de trabajo: http://localhost:8081/api/workorders
@FeignClient(name = "ordenes-trabajo", url = "http://localhost:8081/api/workorders", configuration = FeignClientConfig.class)
public interface OrdenTrabajoClient {

    @PostMapping
    OrdenTrabajoDTO crearOrden(@RequestBody OrdenTrabajoCrearDTO dto);

    @GetMapping("/{id}")
    OrdenTrabajoDTO obtenerOrdenPorId(@PathVariable("id") Long id);

    @PutMapping("/{id}/status")
    OrdenTrabajoDTO actualizarEstado(@PathVariable("id") Long id, @RequestBody ActualizarEstadoDTO dto);
}
