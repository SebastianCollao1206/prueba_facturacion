package com.pe.prueba_facturacion.dto.facturacion;

import com.pe.prueba_facturacion.dto.sunat.DocumentosGeneradosDTO;
import com.pe.prueba_facturacion.model.ComprobanteElectronico;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ComprobanteResponseDTO {

    private Long comprobanteId;
    private String numeroCompleto;
    private String serie;
    private String numero;
    private String tipoComprobante;
    private String estadoSunat;
    private BigDecimal mtoTotal;
    private String razonSocialCliente;
    private String numeroDocCliente;
    private DocumentosGeneradosDTO documentos;

    public ComprobanteResponseDTO(ComprobanteElectronico c) {
        this.comprobanteId      = c.getId();
        this.numeroCompleto     = c.getNumeroCompleto();
        this.serie              = c.getSerie();
        this.numero             = c.getNumero();
        this.tipoComprobante    = c.getTipoComprobante().getNombre();
        this.estadoSunat        = c.getEstadoSunat().name();
        this.mtoTotal           = c.getVenta().getMtoTotal();
        this.razonSocialCliente = c.getVenta().getCliente().getRazonSocial();
        this.numeroDocCliente   = c.getVenta().getCliente().getNumeroDocumento();
        this.documentos         = DocumentosGeneradosDTO.fromEntidad(c);
    }

}
