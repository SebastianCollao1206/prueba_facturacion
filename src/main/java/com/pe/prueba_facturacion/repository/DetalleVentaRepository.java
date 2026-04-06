package com.pe.prueba_facturacion.repository;

import com.pe.prueba_facturacion.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVenta_Id(Long idVenta);
    List<DetalleVenta> findByProducto_Id(Long idProducto);

}
