package com.digitalfix.workorders.service;

import com.digitalfix.workorders.dto.ActualizarEstadoDTO;
import com.digitalfix.workorders.dto.OrdenTrabajoCrearDTO;
import com.digitalfix.workorders.dto.OrdenTrabajoDTO;
import com.digitalfix.workorders.exception.BusinessRuleException;
import com.digitalfix.workorders.exception.ResourceNotFoundException;
import com.digitalfix.workorders.model.EstadoOrden;
import com.digitalfix.workorders.model.OrdenTrabajo;
import com.digitalfix.workorders.repository.OrdenTrabajoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenTrabajoRepository;

    @Override
    public OrdenTrabajoDTO crearOrden(OrdenTrabajoCrearDTO dto) {
        // toma los datos del dto y crea una nueva orden
        OrdenTrabajo orden = OrdenTrabajo.builder()
                .clienteId(dto.getClienteId())
                .servicioId(dto.getServicioId())
                .repuestoId(dto.getRepuestoId())
                .direccion(dto.getDireccion())
                .estado(EstadoOrden.CREADA)
                .build();

        OrdenTrabajo saved = ordenTrabajoRepository.save(orden);
        return mapToDTO(saved);
    }

    @Override
    public OrdenTrabajoDTO obtenerOrdenPorId(Integer id) {
        OrdenTrabajo orden = ordenTrabajoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada"));
        return mapToDTO(orden);
    }

    @Override
    public OrdenTrabajoDTO actualizarEstado(Integer id, ActualizarEstadoDTO dto) {
        OrdenTrabajo orden = ordenTrabajoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de trabajo no encontrada"));

        // verifica obligatoriamente que el estado actual de la entidad sea
        // EstadoOrden.ASIGNADA. Si no lo es, lanza un ReglaNegocioException
        if (dto.getEstado() == EstadoOrden.EN_EJECUCION && orden.getEstado() != EstadoOrden.ASIGNADA) {
            throw new BusinessRuleException("No se puede pasar a EN_EJECUCION sin haber sido ASIGNADA previamente");
        }

        orden.setEstado(dto.getEstado());
        OrdenTrabajo updated = ordenTrabajoRepository.save(orden);

        return mapToDTO(updated);
    }

    private OrdenTrabajoDTO mapToDTO(OrdenTrabajo orden) {
        return OrdenTrabajoDTO.builder()
                .idOrdenTrabajo(orden.getIdOrdenTrabajo())
                .clienteId(orden.getClienteId())
                .servicioId(orden.getServicioId())
                .repuestoId(orden.getRepuestoId())
                .estado(orden.getEstado())
                .direccion(orden.getDireccion())
                .tecnicoAsignadoId(orden.getTecnicoAsignadoId())
                .fechaCreacion(orden.getFechaCreacion())
                .fechaActualizacion(orden.getFechaActualizacion())
                .build();
    }
}
