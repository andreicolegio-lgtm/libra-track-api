package com.libratrack.api.repository;

import com.libratrack.api.entity.CatalogoPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogoPersonalRepository extends JpaRepository<CatalogoPersonal, Long> {

    // --- Métodos Mágicos de Spring Data JPA ---
    // Spring creará el SQL por nosotros basándose en el nombre del método.

    /**
     * Busca todas las entradas del catálogo para un usuario específico.
     * (Necesario para RF08: Mostrar el catálogo personal).
     *
     * @param usuarioId El ID del usuario.
     * @return Una lista de las entradas del catálogo de ese usuario.
     */
    List<CatalogoPersonal> findByUsuarioId(Long usuarioId);

    /**
     * Busca una entrada específica del catálogo combinando el usuario y el elemento.
     * (Necesario para RF05, RF06, RF07: para comprobar si un elemento ya está
     * en el catálogo antes de añadirlo, o para actualizarlo).
     *
     * @param usuarioId El ID del usuario.
     * @param elementoId El ID del elemento.
     * @return Un Optional que contendrá la entrada si ya existe.
     */
    Optional<CatalogoPersonal> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);

}