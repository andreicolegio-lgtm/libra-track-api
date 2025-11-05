package com.libratrack.api.repository;

import com.libratrack.api.entity.Genero;
import java.util.Optional; // Import para Optional
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Genero.
 * Extiende JpaRepository, dándonos métodos CRUD básicos.
 */
@Repository // Le dice a Spring que esta es una interfaz de acceso a datos
public interface GeneroRepository extends JpaRepository<Genero, Long> {

    /**
     * Busca un Genero por su nombre exacto (ej. "Ciencia Ficción").
     * Spring traduce esto a: "SELECT * FROM generos WHERE nombre = ?"
     *
     * Este método es fundamental para el PropuestaElementoService,
     * permitiéndole "traducir" el string 'generosSugeridos' (separado por comas)
     * a un conjunto de entidades Genero reales durante la aprobación (RF15).
     *
     * @param nombre El nombre del Genero a buscar.
     * @return Un 'Optional' que contendrá al Genero si se encuentra.
     */
    Optional<Genero> findByNombre(String nombre);

}