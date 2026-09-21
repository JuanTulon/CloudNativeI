package com.digitalfix.catalogo.service;

import com.digitalfix.catalogo.dto.*;
import java.util.List;

public interface CatalogoService {
    List<ServicioDTO> listarServicios();
    ServicioDTO obtenerServicio(Integer id);
    ServicioDTO crearServicio(ServicioCrearDTO dto);
    ServicioDTO actualizarServicio(Integer id, ServicioActualizarDTO dto);
    List<RepuestoDTO> listarRepuestos();
    RepuestoDTO obtenerRepuesto(Integer id);
    RepuestoDTO crearRepuesto(RepuestoCrearDTO dto);
    RepuestoDTO actualizarRepuesto(Integer id, RepuestoActualizarDTO dto);
}
