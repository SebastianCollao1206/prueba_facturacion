package com.pe.prueba_facturacion.controller;

import com.pe.prueba_facturacion.dto.producto.CrearProductoDTO;
import com.pe.prueba_facturacion.dto.producto.ProductoResponseDTO;
import com.pe.prueba_facturacion.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("/activos")
    public ResponseEntity<List<ProductoResponseDTO>> listarActivos() {
        List<ProductoResponseDTO> productos = productoService.listarActivos();
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@RequestBody @Valid CrearProductoDTO dto) {
        ProductoResponseDTO producto = productoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

}
