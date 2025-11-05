package com.libratrack.api.repository;

import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.model.EstadoPropuesta; // Importa el Enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Import para manejar listas de resultados

/**
 * Repositorio para la entidad PropuestaElemento (la "cola de moderación").
 * Extiende JpaRepository, dándonos métodos CRUD básicos.
 * Implementa el acceso a datos para los requisitos RF13, RF14, RF15.
 */
@Repository
public interface PropuestaElementoRepository extends JpaRepository<PropuestaElemento, Long> {

    /**
     * Busca todas las propuestas que tengan un estado específico.
     * Spring traduce esto a: "SELECT * FROM propuestas_elementos WHERE estado_propuesta = ?"
     *
     * Este es el método fundamental para el Panel de Moderación (RF14),
     * ya que nos permite consultar fácilmente la lista de propuestas
     * que están 'PENDIENTE' de revisión.
     *
     * @param estado El estado a buscar (PENDIENTE, APROBADO, RECHAZADO).
     * @return Una lista de propuestas que coinciden con ese estado.
     */
    List<PropuestaElemento> findByEstadoPropuesta(EstadoPropuesta estado);

}