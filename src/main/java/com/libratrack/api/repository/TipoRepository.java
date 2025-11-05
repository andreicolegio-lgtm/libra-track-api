package com.libratrack.api.repository;

import com.libratrack.api.entity.Tipo;
import java.util.Optional; // Import para Optional
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Tipo.
 * Extiende JpaRepository, dándonos métodos CRUD básicos.
 */
@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {

    /**
     * Busca un Tipo por su nombre exacto (ej. "Serie").
     * Spring traduce esto a: "SELECT * FROM tipos WHERE nombre = ?"
     *
     * Este método es fundamental para el PropuestaElementoService,
     * permitiéndole "traducir" el string 'tipoSugerido' a una
     * entidad Tipo real durante la aprobación (RF15).
     *
     * @param nombre El nombre del Tipo a buscar.
     * @return Un 'Optional' que contendrá al Tipo si se encuentra.
     */
    Optional<Tipo> findByNombre(String nombre);

}