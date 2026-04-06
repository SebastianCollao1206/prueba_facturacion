package com.pe.prueba_facturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "mto_valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal mtoValorUnitario;

    @Column(name = "mto_precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal mtoPrecioUnitario;

    @Column(name = "mto_base_igv", precision = 10, scale = 2)
    private BigDecimal mtoBaseIgv;

    @Column(name = "igv", precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public DetalleVenta(Venta venta, Producto producto, Integer cantidad,
                        BigDecimal mtoValorUnitario, BigDecimal mtoPrecioUnitario,
                        BigDecimal mtoBaseIgv, BigDecimal igv, BigDecimal subtotal) {
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.mtoValorUnitario = mtoValorUnitario;
        this.mtoPrecioUnitario = mtoPrecioUnitario;
        this.mtoBaseIgv = mtoBaseIgv;
        this.igv = igv;
        this.subtotal = subtotal;
    }

}
