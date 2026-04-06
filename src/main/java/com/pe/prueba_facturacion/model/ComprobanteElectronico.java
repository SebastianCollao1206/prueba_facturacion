package com.pe.prueba_facturacion.model;

import com.pe.prueba_facturacion.model.enums.EstadoSunat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comprobante_electronico")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComprobanteElectronico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_venta", nullable = false, unique = true)
    private Venta venta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_comprobante")
    private TipoComprobante tipoComprobante;

    @Column(name = "serie", length = 4)
    private String serie;

    @Column(name = "numero", length = 8)
    private String numero;

    @Column(name = "numero_completo", length = 13)
    private String numeroCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sunat", nullable = false, length = 20)
    private EstadoSunat estadoSunat = EstadoSunat.SIN_EMITIR;

    @Column(name = "xml_sin_firmar", length = 500)
    private String xmlSinFirmar;

    @Column(name = "xml_firmado", length = 500)
    private String xmlFirmado;

//    @Column(name = "xml_url", length = 500)
//    private String xmlUrl;

    @Column(name = "cdr_url", length = 500)
    private String cdrUrl;

    @Column(name = "pdf_a4_url", length = 500)
    private String pdfA4Url;

    @Column(name = "pdf_ticket_url", length = 500)
    private String pdfTicketUrl;

    @Column(name = "hash_cpe", length = 500)
    private String hashCpe;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public ComprobanteElectronico(Venta venta, TipoComprobante tipoComprobante,
                                  String serie, String numero) {
        this.venta = venta;
        this.tipoComprobante = tipoComprobante;
        this.serie = serie;
        this.numero = numero;
        this.numeroCompleto = serie + "-" + numero;
        this.estadoSunat = EstadoSunat.SIN_EMITIR;
    }

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (serie != null && numero != null) {
            this.numeroCompleto = serie + "-" + numero;
        }
    }

}
