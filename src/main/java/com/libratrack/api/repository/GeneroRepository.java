package com.libratrack.api.repository;

import com.libratrack.api.entity.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
    // No necesitamos métodos extra por ahora.
}