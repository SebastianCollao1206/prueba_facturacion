package com.pe.prueba_facturacion.repository;

import com.pe.prueba_facturacion.model.Venta;
import com.pe.prueba_facturacion.model.enums.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByCliente_Id(Long idCliente);
    List<Venta> findByEstado(EstadoVenta estado);

}
