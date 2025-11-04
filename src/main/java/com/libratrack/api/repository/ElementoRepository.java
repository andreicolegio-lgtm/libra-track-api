package com.libratrack.api.repository;

import com.libratrack.api.entity.Elemento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElementoRepository extends JpaRepository<Elemento, Long> {
    // Al extender JpaRepository, ya tenemos gratis:
    // - save(elemento)
    // - findById(id)
    // - findAll()
    // - deleteById(id)
    
    // Podemos añadir métodos mágicos si los necesitamos, por ejemplo:
    // List<Elemento> findByTitulo(String titulo);
}