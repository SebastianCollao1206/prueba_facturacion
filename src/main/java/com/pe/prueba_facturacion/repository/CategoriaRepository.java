package com.pe.prueba_facturacion.repository;

import com.pe.prueba_facturacion.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombre(String nombre);
    List<Categoria> findByEstado(Boolean estado);

}
