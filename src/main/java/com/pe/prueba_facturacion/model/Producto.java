package com.pe.prueba_facturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "imagen", length = 500)
    private String imagen;

    @Column(name = "tipo_afect_igv", nullable = false, length = 4)
    private String tipoAfectIgv = "10";

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(nullable = false)
    private Integer stock;

    public Producto(Categoria categoria, String descripcion, BigDecimal precio, String imagen, Integer stock) {
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
        this.tipoAfectIgv = "10";
        this.estado = true;
        this.stock = stock;
    }

}
