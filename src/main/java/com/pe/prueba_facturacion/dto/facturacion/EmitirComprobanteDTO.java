package com.pe.prueba_facturacion.dto.facturacion;

import lombok.Data;

@Data
public class EmitirComprobanteDTO {

    private Long ventaId;
    private String tipoComprobante;

}
