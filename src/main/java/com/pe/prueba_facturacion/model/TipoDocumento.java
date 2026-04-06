package com.pe.prueba_facturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_documento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2, unique = true)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    public TipoDocumento(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

}
