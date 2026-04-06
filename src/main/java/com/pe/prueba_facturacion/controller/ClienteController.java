package com.pe.prueba_facturacion.controller;

import com.pe.prueba_facturacion.dto.cliente.CrearCliente;
import com.pe.prueba_facturacion.model.Cliente;
import com.pe.prueba_facturacion.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping("/obtener-o-crear")
    public ResponseEntity<?> obtenerOCrearCliente(@Valid @RequestBody CrearCliente dto) {
        try {
            Cliente cliente = clienteService.obtenerOCrearCliente(dto);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
