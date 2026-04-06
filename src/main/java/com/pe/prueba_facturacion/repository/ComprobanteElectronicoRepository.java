package com.pe.prueba_facturacion.repository;

import com.pe.prueba_facturacion.model.ComprobanteElectronico;
import com.pe.prueba_facturacion.model.Venta;
import com.pe.prueba_facturacion.model.enums.EstadoSunat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobanteElectronicoRepository extends JpaRepository<ComprobanteElectronico, Long> {

    Optional<ComprobanteElectronico> findByVenta(Venta venta);
    Optional<ComprobanteElectronico> findTopBySerieOrderByNumeroDesc(String serie);

}
