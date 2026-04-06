package com.pe.prueba_facturacion.dto.exeptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private String mensaje;
    private String detalle;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String mensaje, String detalle, int status) {
        this.mensaje = mensaje;
        this.detalle = detalle;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
