package com.pe.prueba_facturacion.dto.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearCliente {

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 255)
    private String razonSocial;

    private String dni;
    private String ruc;
    private String pasaporte;
    private String carneExtranjeria;

    @Size(max = 300)
    private String direccionFiscal;

    private String codigoPais = "PE";

}
