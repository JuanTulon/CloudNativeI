package com.digitalfix.catalogo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 120)
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tarifa;
    @Column(nullable = false)
    private Boolean activo;
}
