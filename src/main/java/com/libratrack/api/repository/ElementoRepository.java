// Archivo: src/main/java/com/libratrack/api/repository/ElementoRepository.java
// (¡MODIFICADO POR GEMINI PARA CORREGIR LAZYINITIALIZATION!)

package com.libratrack.api.repository;

import com.libratrack.api.entity.Elemento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
// NUEVAS IMPORTACIONES
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ElementoRepository extends JpaRepository<Elemento, Long> {
    
    // --- MÉTODO ANTIGUO (Lo dejamos por si lo usa otro servicio) ---
    List<Elemento> findByTituloContainingIgnoreCase(String titulo);

    // --- ¡MÉTODO DE BÚSQUEDA CORREGIDO! ---
    /**
     * Busca elementos filtrando por todos los criterios y devuelve un resultado paginado.
     * Esta consulta es la implementación de rendimiento de RF09.
     *
     * --- ¡CORREGIDO POR GEMINI (ID: 17112025-A)! ---
     * Se ha modificado la consulta para solucionar LazyInitializationException:
     * 1. Se añade 'DISTINCT' para evitar duplicados en el resultado.
     * 2. Se añaden 'LEFT JOIN FETCH e.tipo' y 'LEFT JOIN FETCH e.generos' para
     * cargar estas relaciones (que son LAZY) en la misma consulta,
     * evitando el error de "no session".
     * 3. Se mantienen los 'LEFT JOIN' simples (t_filter, g_filter) para
     * usarlos *solo* en las cláusulas WHERE de filtrado.
     * 4. Se añade un 'countQuery' personalizado para que la paginación
     * funcione correctamente con los JOIN FETCH.
     */
    @Query(value = "SELECT DISTINCT e FROM Elemento e " +
                   "LEFT JOIN FETCH e.tipo " +      // <-- FETCH para cargar el Tipo
                   "LEFT JOIN FETCH e.generos " +   // <-- FETCH para cargar Generos
                   // Joins separados para FILTRAR (sin FETCH)
                   "LEFT JOIN e.tipo t_filter " + 
                   "LEFT JOIN e.generos g_filter " +
                   "WHERE " +
                   // 1. Filtro de Título (searchText)
                   "(LOWER(e.titulo) LIKE LOWER(CONCAT('%', COALESCE(:searchText, ''), '%'))) " +
                   
                   // 2. Filtro de Tipo (tipoName)
                   "AND (:tipoName IS NULL OR t_filter.nombre = :tipoName) " +
                   
                   // 3. Filtro de Género (generoName)
                   "AND (:generoName IS NULL OR g_filter.nombre = :generoName)",
           
           // Query de conteo separada para que la paginación funcione
           countQuery = "SELECT COUNT(DISTINCT e.id) FROM Elemento e " +
                        "LEFT JOIN e.tipo t_filter " +
                        "LEFT JOIN e.generos g_filter " +
                        "WHERE " +
                        "(LOWER(e.titulo) LIKE LOWER(CONCAT('%', COALESCE(:searchText, ''), '%'))) " +
                        "AND (:tipoName IS NULL OR t_filter.nombre = :tipoName) " + 
                        "AND (:generoName IS NULL OR g_filter.nombre = :generoName)")
    Page<Elemento> findElementosByFiltros(
            @Param("searchText") String searchText, 
            @Param("tipoName") String tipoName, 
            @Param("generoName") String generoName, 
            Pageable pageable);
}