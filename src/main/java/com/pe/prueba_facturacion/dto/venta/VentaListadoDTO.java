package com.pe.prueba_facturacion.dto.venta;

import com.pe.prueba_facturacion.model.ComprobanteElectronico;
import com.pe.prueba_facturacion.model.Venta;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VentaListadoDTO {

    private Long ventaId;
    private String cliente;
    private LocalDate fechaEmision;
    private BigDecimal mtoTotal;
    private String tipoPago;
    private String estadoVenta;
    private String estadoSunat;
    private String numeroCompleto;
    private String tipoComprobante;
    private String xmlSinFirmar;
    private String xmlFirmado;
    private String pdfA4Url;
    private String pdfTicketUrl;
    private String cdrUrl;

    public VentaListadoDTO(Venta venta, ComprobanteElectronico comp) {
        this.ventaId       = venta.getId();
        this.cliente       = venta.getCliente().getRazonSocial();
        this.fechaEmision  = venta.getFechaEmision();
        this.mtoTotal      = venta.getMtoTotal();
        this.tipoPago      = venta.getTipoPago();
        this.estadoVenta   = venta.getEstado().name();
        this.estadoSunat     = comp.getEstadoSunat().name();
        this.numeroCompleto  = comp.getNumeroCompleto();
        this.tipoComprobante = comp.getTipoComprobante() != null
                ? comp.getTipoComprobante().getNombre() : null;
        this.xmlSinFirmar    = comp.getXmlSinFirmar();
        this.xmlFirmado      = comp.getXmlFirmado();
        this.pdfA4Url        = comp.getPdfA4Url();
        this.pdfTicketUrl    = comp.getPdfTicketUrl();
        this.cdrUrl          = comp.getCdrUrl();
    }

}
