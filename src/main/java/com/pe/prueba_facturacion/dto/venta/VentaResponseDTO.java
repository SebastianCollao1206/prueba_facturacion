package com.pe.prueba_facturacion.dto.venta;

import com.pe.prueba_facturacion.model.ComprobanteElectronico;
import com.pe.prueba_facturacion.model.Venta;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VentaResponseDTO {

    private Long ventaId;
    private LocalDate fechaEmision;
    private String estado;
    private BigDecimal mtoOpGravadas;
    private BigDecimal mtoIgv;
    private BigDecimal mtoTotal;
    private String tipoPago;
    private Long comprobanteId;
    private String estadoSunat;

    public VentaResponseDTO(Venta venta, ComprobanteElectronico comprobante) {
        this.ventaId = venta.getId();
        this.fechaEmision = venta.getFechaEmision();
        this.estado = venta.getEstado().name();
        this.mtoOpGravadas = venta.getMtoOpGravadas();
        this.mtoIgv = venta.getMtoIgv();
        this.mtoTotal = venta.getMtoTotal();
        this.tipoPago = venta.getTipoPago();
        this.comprobanteId = comprobante.getId();
        this.estadoSunat = comprobante.getEstadoSunat().name();
    }

}
