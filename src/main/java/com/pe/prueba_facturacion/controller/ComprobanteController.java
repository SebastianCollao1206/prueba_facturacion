package com.pe.prueba_facturacion.controller;

import com.pe.prueba_facturacion.dto.facturacion.ComprobanteResponseDTO;
import com.pe.prueba_facturacion.dto.facturacion.EmitirComprobanteDTO;
import com.pe.prueba_facturacion.service.ComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    @PostMapping("/emitir")
    public ResponseEntity<ComprobanteResponseDTO> emitir(
            @RequestBody EmitirComprobanteDTO dto) {

        ComprobanteResponseDTO response = comprobanteService.emitirComprobante(dto);
        return ResponseEntity.ok(response);
    }

}
