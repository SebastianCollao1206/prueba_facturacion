package com.pe.prueba_facturacion.model;

import com.pe.prueba_facturacion.model.enums.EstadoVenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "hora_emision", nullable = false)
    private LocalTime horaEmision;

    @Column(name = "tipo_pago", nullable = false, length = 20)
    private String tipoPago;  // Contado, Credito

    @Column(name = "tipo_moneda", nullable = false, length = 3)
    private String tipoMoneda = "PEN";

    @Column(name = "tipo_operacion", nullable = false, length = 4)
    private String tipoOperacion = "0101";

    @Column(name = "mto_op_gravadas", precision = 10, scale = 2)
    private BigDecimal mtoOpGravadas = BigDecimal.ZERO;

    @Column(name = "mto_op_exoneradas", precision = 10, scale = 2)
    private BigDecimal mtoOpExoneradas = BigDecimal.ZERO;

    @Column(name = "mto_op_inafectas", precision = 10, scale = 2)
    private BigDecimal mtoOpInafectas = BigDecimal.ZERO;

    @Column(name = "mto_igv", precision = 10, scale = 2)
    private BigDecimal mtoIgv = BigDecimal.ZERO;

    @Column(name = "mto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal mtoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoVenta estado = EstadoVenta.REGISTRADA;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta(Cliente cliente, String tipoPago, BigDecimal mtoOpGravadas,
                 BigDecimal mtoOpExoneradas, BigDecimal mtoOpInafectas,
                 BigDecimal mtoIgv, BigDecimal mtoTotal) {
        this.cliente = cliente;
        this.tipoPago = tipoPago;
        this.tipoMoneda = "PEN";
        this.tipoOperacion = "0101";
        this.mtoOpGravadas = mtoOpGravadas;
        this.mtoOpExoneradas = mtoOpExoneradas;
        this.mtoOpInafectas = mtoOpInafectas;
        this.mtoIgv = mtoIgv;
        this.mtoTotal = mtoTotal;
        this.estado = EstadoVenta.REGISTRADA;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaEmision == null) {
            fechaEmision = LocalDate.now();
        }
        if (horaEmision == null) {
            horaEmision = LocalTime.now();
        }
    }

}
