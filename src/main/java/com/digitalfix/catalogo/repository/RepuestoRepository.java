package com.digitalfix.catalogo.repository;

import com.digitalfix.catalogo.model.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepuestoRepository extends JpaRepository<Repuesto, Integer> {
    boolean existsByCodigo(String codigo);
}
