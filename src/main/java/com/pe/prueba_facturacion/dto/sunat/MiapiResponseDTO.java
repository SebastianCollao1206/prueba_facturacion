package com.pe.prueba_facturacion.dto.sunat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiapiResponseDTO {

    private Boolean success;
    private String mensaje;
    private String error;

    @JsonProperty("cdr")
    private String cdrUrl;

    @JsonProperty("hash")
    private String hashCpe;

    @JsonProperty("respuesta")
    private MiapiResponseDTO respuesta;

    public MiapiResponseDTO unwrap() {
        return (respuesta != null) ? respuesta : this;
    }

}
