package com.libratrack.api.repository;

import com.libratrack.api.entity.Resena; // Importa la entidad Resena
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import para manejar listas de resultados
import java.util.Optional; // Import para manejar resultados que pueden ser nulos

/**
 * Repositorio para la entidad Resena.
 * Extiende JpaRepository, dándonos métodos CRUD básicos (save, findById, etc.).
 * Implementa el acceso a datos para el requisito RF12.
 */
@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    /**
     * Busca todas las reseñas de un elemento específico (RF12).
     *
     * Este es un método "mágico" de Spring Data JPA. Automáticamente genera
     * la consulta SQL:
     * "SELECT * FROM resenas WHERE elemento_id = ? ORDER BY fecha_creacion DESC"
     *
     * @param elementoId El ID del elemento del cual queremos las reseñas.
     * @return Una lista de reseñas, ordenadas de la más nueva a la más antigua.
     */
    List<Resena> findByElementoIdOrderByFechaCreacionDesc(Long elementoId);

    /**
     * Busca una reseña específica de un usuario para un elemento.
     * Spring traduce esto a: "SELECT * FROM resenas WHERE usuario_id = ? AND elemento_id = ?"
     *
     * Este método es fundamental para el ResenaService, ya que nos permite
     * verificar si un usuario ya ha reseñado un elemento, cumpliendo así
     * la restricción 'uniqueConstraint' de la entidad Resena.
     *
     * @param usuarioId El ID del usuario.
     * @param elementoId El ID del elemento.
     * @return Un Optional que contendrá la reseña si ya existe.
     */
    Optional<Resena> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);
}