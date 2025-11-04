package com.libratrack.api.repository;

import com.libratrack.api.entity.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
    // No necesitamos métodos extra por ahora.
    // JpaRepository es suficiente.
}