package com.pe.prueba_facturacion.dto.sunat;

import com.pe.prueba_facturacion.model.ComprobanteElectronico;
import lombok.Data;

@Data
public class DocumentosGeneradosDTO {

    private String xmlSinFirmar;
    private String xmlFirmado;
    private String pdfA4Url;
    private String pdfTicketUrl;

    public static DocumentosGeneradosDTO fromEntidad(ComprobanteElectronico c) {
        DocumentosGeneradosDTO dto = new DocumentosGeneradosDTO();
        dto.xmlSinFirmar = c.getXmlSinFirmar();
        dto.xmlFirmado   = c.getXmlFirmado();
        dto.pdfA4Url     = c.getPdfA4Url();
        dto.pdfTicketUrl = c.getPdfTicketUrl();
        return dto;
    }

}
