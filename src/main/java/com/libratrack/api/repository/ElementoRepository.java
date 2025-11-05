package com.libratrack.api.repository;

import com.libratrack.api.entity.Elemento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// (Podríamos añadir 'org.springframework.data.domain.Page' y 'Pageable'
// si quisiéramos implementar paginación en el futuro).

/**
 * Repositorio para la entidad Elemento.
 * Extiende JpaRepository, dándonos métodos CRUD (Create, Read, Update, Delete)
 * listos para usar, como:
 * - save(elemento): Guarda o actualiza un elemento.
 * - findById(id): Busca un elemento por su ID (usado para RF10).
 * - findAll(): Busca todos los elementos (usado para RF09).
 * - deleteById(id): Borra un elemento.
 */
@Repository
public interface ElementoRepository extends JpaRepository<Elemento, Long> {
    
    // --- Métodos Mágicos (Query Methods) ---
    // En el futuro, si quisiéramos una búsqueda más avanzada (RF09),
    // podríamos añadir métodos mágicos aquí, por ejemplo:
    //
    // Page<Elemento> findByTituloContaining(String titulo, Pageable pageable);
    // (Esto buscaría elementos cuyo título 'contenga' el texto de búsqueda
    // y devolvería los resultados paginados).
    //
    // Por ahora, el 'findAll()' básico es suficiente.
}