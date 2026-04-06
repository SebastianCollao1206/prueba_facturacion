package com.pe.prueba_facturacion.repository;

import com.pe.prueba_facturacion.model.FacturacionConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturacionConfiguracionRepository extends JpaRepository<FacturacionConfiguracion, Long> {

    Optional<FacturacionConfiguracion> findByEstadoTrue();

}
