package com.pe.prueba_facturacion.service;

import com.pe.prueba_facturacion.dto.producto.CrearProductoDTO;
import com.pe.prueba_facturacion.dto.producto.ProductoResponseDTO;
import com.pe.prueba_facturacion.model.Categoria;
import com.pe.prueba_facturacion.model.Producto;
import com.pe.prueba_facturacion.repository.CategoriaRepository;
import com.pe.prueba_facturacion.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarActivos() {
        return productoRepository.findByEstado(true).stream()
                .map(ProductoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoResponseDTO crear(CrearProductoDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if (!categoria.getEstado()) {
            throw new IllegalArgumentException("La categoría no está activa");
        }

        Producto producto = new Producto(
                categoria,
                dto.getDescripcion(),
                dto.getPrecio(),
                dto.getImagen(),
                dto.getStock()
        );

        Producto productoGuardado = productoRepository.save(producto);
        log.info("Producto creado con id: {}", productoGuardado.getId());
        return new ProductoResponseDTO(productoGuardado);
    }

}
