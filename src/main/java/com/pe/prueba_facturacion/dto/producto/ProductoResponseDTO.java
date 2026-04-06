package com.pe.prueba_facturacion.dto.producto;

import com.pe.prueba_facturacion.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {

    private Long id;
    private String categoria;
    private String descripcion;
    private BigDecimal precio;
    private String imagen;
    private String tipoAfectIgv;
    private Boolean estado;
    private Integer stock;

    public ProductoResponseDTO(Producto producto) {
        this.id = producto.getId();
        this.categoria = producto.getCategoria().getNombre();
        this.descripcion = producto.getDescripcion();
        this.precio = producto.getPrecio();
        this.imagen = producto.getImagen();
        this.tipoAfectIgv = producto.getTipoAfectIgv();
        this.estado = producto.getEstado();
        this.stock = producto.getStock();
    }

}
