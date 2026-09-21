package com.digitalfix.catalogo.service;

import com.digitalfix.catalogo.dto.*;
import com.digitalfix.catalogo.exception.*;
import com.digitalfix.catalogo.model.*;
import com.digitalfix.catalogo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {
    private final ServicioRepository servicioRepository;
    private final RepuestoRepository repuestoRepository;

    public List<ServicioDTO> listarServicios() { return servicioRepository.findAll().stream().map(ServicioDTO::from).toList(); }
    public ServicioDTO obtenerServicio(Integer id) { return ServicioDTO.from(servicioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"))); }
    public ServicioDTO crearServicio(ServicioCrearDTO dto) {
        Servicio s = Servicio.builder().nombre(dto.getNombre()).descripcion(dto.getDescripcion()).tarifa(dto.getTarifa()).activo(true).build();
        return ServicioDTO.from(servicioRepository.save(s));
    }
    public ServicioDTO actualizarServicio(Integer id, ServicioActualizarDTO dto) {
        Servicio s = servicioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        s.setNombre(dto.getNombre()); s.setDescripcion(dto.getDescripcion()); s.setTarifa(dto.getTarifa()); s.setActivo(dto.getActivo());
        return ServicioDTO.from(servicioRepository.save(s));
    }
    public List<RepuestoDTO> listarRepuestos() { return repuestoRepository.findAll().stream().map(RepuestoDTO::from).toList(); }
    public RepuestoDTO obtenerRepuesto(Integer id) { return RepuestoDTO.from(repuestoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Repuesto no encontrado"))); }
    public RepuestoDTO crearRepuesto(RepuestoCrearDTO dto) {
        if (repuestoRepository.existsByCodigo(dto.getCodigo())) throw new BusinessRuleException("Ya existe un repuesto con el código indicado");
        Repuesto r = Repuesto.builder().codigo(dto.getCodigo()).nombre(dto.getNombre()).descripcion(dto.getDescripcion()).stock(dto.getStock()).precio(dto.getPrecio()).activo(true).build();
        return RepuestoDTO.from(repuestoRepository.save(r));
    }
    public RepuestoDTO actualizarRepuesto(Integer id, RepuestoActualizarDTO dto) {
        Repuesto r = repuestoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Repuesto no encontrado"));
        if (!r.getCodigo().equals(dto.getCodigo()) && repuestoRepository.existsByCodigo(dto.getCodigo())) throw new BusinessRuleException("Ya existe un repuesto con el código indicado");
        r.setCodigo(dto.getCodigo()); r.setNombre(dto.getNombre()); r.setDescripcion(dto.getDescripcion()); r.setStock(dto.getStock()); r.setPrecio(dto.getPrecio()); r.setActivo(dto.getActivo());
        return RepuestoDTO.from(repuestoRepository.save(r));
    }
}
