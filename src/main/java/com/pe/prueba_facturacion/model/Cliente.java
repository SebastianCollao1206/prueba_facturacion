package com.pe.prueba_facturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_doc", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "razon_social", nullable = false, length = 255)
    private String razonSocial;

    @Column(name = "num_doc", length = 20)
    private String numeroDocumento;

    @Column(name = "direccion_fiscal", length = 300)
    private String direccionFiscal;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "codigo_pais", nullable = false, length = 2)
    private String codigoPais = "PE";

    public Cliente(TipoDocumento tipoDocumento, String razonSocial, String numeroDocumento, String direccionFiscal, String codigoPais) {
        this.tipoDocumento = tipoDocumento;
        this.razonSocial = razonSocial;
        this.numeroDocumento = numeroDocumento;
        this.direccionFiscal = direccionFiscal;
        this.codigoPais = "PE";
    }

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

}
