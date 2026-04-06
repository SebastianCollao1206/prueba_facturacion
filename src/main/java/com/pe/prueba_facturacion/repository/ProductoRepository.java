package com.pe.prueba_facturacion.repository;

import com.pe.prueba_facturacion.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEstado(Boolean estado);
    List<Producto> findByCategoria_Id(Long idCategoria);
    List<Producto> findByDescripcionContainingIgnoreCase(String descripcion);

}
