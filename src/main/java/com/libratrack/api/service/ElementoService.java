// Archivo: src/main/java/com/libratrack/api/service/ElementoService.java
// (¡MODIFICADO POR GEMINI!)

package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoFormDTO; 
import com.libratrack.api.dto.ElementoResponseDTO;
// --- ¡NUEVA IMPORTACIÓN! ---
import com.libratrack.api.dto.ElementoRelacionDTO; 
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ResourceNotFoundException; 
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.hibernate.Hibernate; 

// --- ¡NUEVAS IMPORTACIONES! ---
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
// ---

import java.util.Optional;
import java.util.Set;

/**
 * --- ¡ACTUALIZADO (Sprint 10 / Relaciones)! ---
 */
@Service
public class ElementoService {
    
    @Autowired private ElementoRepository elementoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    
    @Autowired private PropuestaElementoService propuestaService; 
    
    /**
     * Busca todos los elementos (paginado) (RF09).
     * (Corregido con JOIN FETCH en el Repository).
     */
    public Page<ElementoResponseDTO> findAllElementos(Pageable pageable, String searchText, String tipoName, String generoName) {
        Page<Elemento> paginaDeElementos = elementoRepository.findElementosByFiltros(
            searchText, 
            tipoName, 
            generoName, 
            pageable
        );
        return paginaDeElementos.map(ElementoResponseDTO::new);
    }
    
    /**
     * Busca un elemento por su ID (RF10).
     * (Corregido con inicialización LAZY explícita).
     */
    @Transactional(readOnly = true) 
    public Optional<ElementoResponseDTO> findElementoById(Long id) {
        
        Optional<Elemento> elementoOptional = elementoRepository.findById(id);
        
        if (elementoOptional.isEmpty()) {
            return Optional.empty();
        }
        
        Elemento elemento = elementoOptional.get();
        
        // Forzamos la inicialización de TODAS las colecciones LAZY
        Hibernate.initialize(elemento.getTipo());
        Hibernate.initialize(elemento.getGeneros());
        Hibernate.initialize(elemento.getCreador()); 
        Hibernate.initialize(elemento.getPrecuelas());
        Hibernate.initialize(elemento.getSecuelas());
        
        return Optional.of(new ElementoResponseDTO(elemento));
    }

    // --- ¡NUEVO MÉTODO! (Añadido por Gemini) ---
    /**
     * Devuelve una lista simple de todos los elementos (id, titulo, imagen)
     * para rellenar selectores en el frontend.
     */
    @Transactional(readOnly = true)
    public List<ElementoRelacionDTO> findAllSimple() {
        // Usamos una consulta simple para evitar cargar entidades completas
        List<Elemento> elementos = elementoRepository.findAll(Sort.by("titulo").ascending());
        
        // Mapeamos a ElementoRelacionDTO (que es superficial)
        return elementos.stream()
                .map(ElementoRelacionDTO::new)
                .collect(Collectors.toList());
    }
    // --- FIN DE MÉTODO AÑADIDO ---
    
    
    // --- MÉTODOS DE ADMIN/MOD (Modificados) ---
    
    /**
     * (Petición 15) Crea un nuevo Elemento directamente como OFICIAL.
     * Ya es @Transactional.
     */
    @Transactional
    public ElementoResponseDTO crearElementoOficial(ElementoFormDTO dto, String adminUsername) {
        Usuario admin = usuarioRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin no encontrado."));
        
        Elemento nuevoElemento = new Elemento();
        
        // ¡Este método ahora también mapea las secuelas!
        mapElementoFromFormDTO(nuevoElemento, dto); 
        
        nuevoElemento.setCreador(admin); 
        nuevoElemento.setEstadoContenido(EstadoContenido.OFICIAL); 
        nuevoElemento.setEstadoPublicacion(EstadoPublicacion.DISPONIBLE); 

        Elemento elementoGuardado = elementoRepository.save(nuevoElemento);
        
        // Devolvemos el DTO completo, que incluye las relaciones
        return new ElementoResponseDTO(elementoGuardado);
    }

    /**
     * (Petición 8) Actualiza un Elemento existente.
     * Ya es @Transactional.
     */
    @Transactional
    public ElementoResponseDTO updateElemento(Long elementoId, ElementoFormDTO dto) {
        Elemento elemento = elementoRepository.findById(elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId));
        
        // ¡Este método ahora también mapea las secuelas!
        mapElementoFromFormDTO(elemento, dto); 
        
        Elemento elementoGuardado = elementoRepository.save(elemento);
        
        // Devolvemos el DTO completo, que incluye las relaciones
        // Usamos findElementoById para asegurarnos de que TODAS las
        // relaciones (incluidas las precuelas) están cargadas.
        return findElementoById(elementoGuardado.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Error al recargar el elemento actualizado."));
    }
    
    // ... (oficializarElemento, comunitarizarElemento sin cambios) ...

    @Transactional
    public ElementoResponseDTO oficializarElemento(Long elementoId) {
        Elemento elemento = elementoRepository.findById(elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId));
        elemento.setEstadoContenido(EstadoContenido.OFICIAL);
        Elemento elementoGuardado = elementoRepository.save(elemento);
        // Usamos findElementoById para una respuesta consistente
        return findElementoById(elementoGuardado.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Error al recargar el elemento actualizado."));
    }
    
    @Transactional
    public ElementoResponseDTO comunitarizarElemento(Long elementoId) {
        Elemento elemento = elementoRepository.findById(elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId));
        elemento.setEstadoContenido(EstadoContenido.COMUNITARIO); 
        Elemento elementoGuardado = elementoRepository.save(elemento);
        // Usamos findElementoById para una respuesta consistente
        return findElementoById(elementoGuardado.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Error al recargar el elemento actualizado."));
    }
    
    
    /**
     * (Petición 8) Método helper para mapear los campos
     * del DTO a la entidad Elemento.
     * --- ¡ACTUALIZADO POR GEMINI! ---
     */
    private void mapElementoFromFormDTO(Elemento elemento, ElementoFormDTO dto) {
        // 1. Mapeo de campos simples y traducción
        Tipo tipo = propuestaService.traducirTipo(dto.getTipoNombre());
        Set<Genero> generos = propuestaService.traducirGeneros(dto.getGenerosNombres());
        
        elemento.setTitulo(dto.getTitulo());
        elemento.setDescripcion(dto.getDescripcion());
        elemento.setUrlImagen(dto.getUrlImagen());
        elemento.setTipo(tipo);
        elemento.setGeneros(generos);
        
        // 2. Mapeo de Progreso
        elemento.setEpisodiosPorTemporada(dto.getEpisodiosPorTemporada());
        elemento.setTotalUnidades(dto.getTotalUnidades());
        elemento.setTotalCapitulosLibro(dto.getTotalCapitulosLibro());
        elemento.setTotalPaginasLibro(dto.getTotalPaginasLibro());

        // 3. --- ¡NUEVA LÓGICA DE RELACIONES! ---
        // (Esto funciona porque el método padre es @Transactional)
        
        // Limpiamos las secuelas existentes
        elemento.getSecuelas().clear();

        // Si el DTO trae una lista de IDs de secuelas...
        if (dto.getSecuelaIds() != null && !dto.getSecuelaIds().isEmpty()) {
            // Buscamos todas las entidades Elemento por sus IDs
            List<Elemento> secuelas = elementoRepository.findAllById(dto.getSecuelaIds());
            // Y las añadimos al Set
            elemento.setSecuelas(new HashSet<>(secuelas));
        }
        // Si la lista es nula o vacía, el Set ya está limpio.
        // --- FIN DE NUEVA LÓGICA ---
    }
}