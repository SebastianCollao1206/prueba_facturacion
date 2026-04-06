package com.pe.prueba_facturacion.repository;

import com.pe.prueba_facturacion.model.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoComprobanteRepository extends JpaRepository<TipoComprobante, Long> {

    Optional<TipoComprobante> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

}
