package com.pe.prueba_facturacion.controller;

import com.pe.prueba_facturacion.dto.sunat.MiapiResponseDTO;
import com.pe.prueba_facturacion.service.SunatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sunat")
@RequiredArgsConstructor
public class SunatController {

    private final SunatService sunatService;

    @PostMapping("/enviar/{comprobanteId}")
    public ResponseEntity<?> enviarASunat(@PathVariable Long comprobanteId) {
        try {
            MiapiResponseDTO respuesta = sunatService.enviarASunat(comprobanteId);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    java.util.Map.of("error", "Error al comunicarse con SUNAT: " + e.getMessage()));
        }
    }

}

