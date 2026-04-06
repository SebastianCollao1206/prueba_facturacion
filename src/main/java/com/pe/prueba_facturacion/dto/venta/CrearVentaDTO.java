package com.pe.prueba_facturacion.dto.venta;

import com.pe.prueba_facturacion.dto.cliente.CrearCliente;
import lombok.Data;

import java.util.List;

@Data
public class CrearVentaDTO {

    private CrearCliente cliente;
    private String tipoPago; // "Contado" o "Credito"
    private List<DetalleVentaDTO> detalles;

}
