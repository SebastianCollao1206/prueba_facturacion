package com.pe.prueba_facturacion.controller;

import com.pe.prueba_facturacion.dto.venta.CrearVentaDTO;
import com.pe.prueba_facturacion.dto.venta.VentaListadoDTO;
import com.pe.prueba_facturacion.dto.venta.VentaResponseDTO;
import com.pe.prueba_facturacion.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaListadoDTO>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@RequestBody CrearVentaDTO dto) {
        return ResponseEntity.ok(ventaService.crearVenta(dto));
    }

}
