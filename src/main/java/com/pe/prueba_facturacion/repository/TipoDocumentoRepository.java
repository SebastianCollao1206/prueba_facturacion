package com.pe.prueba_facturacion.repository;

import com.pe.prueba_facturacion.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {

    Optional<TipoDocumento> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

}
