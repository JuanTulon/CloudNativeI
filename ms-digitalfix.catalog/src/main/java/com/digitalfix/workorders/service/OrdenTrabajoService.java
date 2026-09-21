package com.digitalfix.workorders.service;

import com.digitalfix.workorders.dto.ActualizarEstadoDTO;
import com.digitalfix.workorders.dto.OrdenTrabajoCrearDTO;
import com.digitalfix.workorders.dto.OrdenTrabajoDTO;

public interface OrdenTrabajoService {
    // esto se comunica despues con el controler para el controler solo tener
    // acceso a estos nombres de metodos y no al codigo interno de cada uno
    OrdenTrabajoDTO crearOrden(OrdenTrabajoCrearDTO dto);

    OrdenTrabajoDTO obtenerOrdenPorId(Integer id);

    OrdenTrabajoDTO actualizarEstado(Integer id, ActualizarEstadoDTO dto);
}
