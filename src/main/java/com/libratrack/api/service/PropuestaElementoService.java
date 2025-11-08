// Archivo: src/main/java/com/libratrack/api/service/PropuestaElementoService.java
package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.dto.PropuestaRequestDTO;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.entity.*;
import com.libratrack.api.exception.ConflictException; // NUEVA IMPORTACIÓN
import com.libratrack.api.exception.ResourceNotFoundException; // NUEVA IMPORTACIÓN
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPropuesta;
import com.libratrack.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para la lógica de negocio de la cola de moderación.
 * REFACTORIZADO: Usa excepciones de negocio (ResourceNotFound/Conflict).
 */
@Service
public class PropuestaElementoService {

    // --- Inyección de Dependencias ---
    // (Código de inyección sin cambios)

    @Autowired
    private PropuestaElementoRepository propuestaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ElementoRepository elementoRepo;

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private GeneroRepository generoRepository;

    /**
     * Crea una nueva propuesta y la añade a la cola de moderación (RF13).
     */
    public PropuestaResponseDTO createPropuesta(PropuestaRequestDTO dto, String proponenteUsername) { // Eliminamos 'throws Exception'
        // 1. Buscar al usuario proponente por su nombre (del token)
        Usuario proponente = usuarioRepo.findByUsername(proponenteUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario proponente no encontrado.")); // <-- 404
        
        // 2. Crear la nueva entidad Propuesta
        PropuestaElemento nuevaPropuesta = new PropuestaElemento();
        nuevaPropuesta.setProponente(proponente);
        nuevaPropuesta.setTituloSugerido(dto.getTituloSugerido());
        nuevaPropuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
        nuevaPropuesta.setTipoSugerido(dto.getTipoSugerido());
        nuevaPropuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
        
        // 3. Guardar en la tabla 'propuestas_elementos'
        PropuestaElemento propuestaGuardada = propuestaRepo.save(nuevaPropuesta);
        
        // 4. Devolver el DTO de Respuesta
        return new PropuestaResponseDTO(propuestaGuardada);
    }

    /**
     * Obtiene la lista de propuestas pendientes (RF14 - Panel de Moderación).
     * (Código sin cambios)
     */
    public List<PropuestaResponseDTO> getPropuestasPendientes() {
        List<PropuestaElemento> propuestas = propuestaRepo.findByEstadoPropuesta(EstadoPropuesta.PENDIENTE);
        return propuestas.stream()
                .map(PropuestaResponseDTO::new)
                .collect(Collectors.toList());
    }


    /**
     * Aprueba una propuesta (RF15).
     */
    @Transactional
    public ElementoResponseDTO aprobarPropuesta(Long propuestaId, Long revisorId) { // Eliminamos 'throws Exception'
        
        // 1. Buscar al moderador (revisor) (404)
        Usuario revisor = usuarioRepo.findById(revisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario revisor no encontrado.")); // <-- 404

        // 2. Buscar la propuesta (404)
        PropuestaElemento propuesta = propuestaRepo.findById(propuestaId)
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta no encontrada con id: " + propuestaId)); // <-- 404

        // 3. Validación de estado (409)
        if (propuesta.getEstadoPropuesta() != EstadoPropuesta.PENDIENTE) {
            throw new ConflictException("Esta propuesta ya ha sido gestionada."); // <-- 409
        }

        // 4. --- INICIO DE LA LÓGICA DE "TRADUCCIÓN" (RF15) ---

        // 4a. Traducir el TIPO (409 si es nulo/vacío)
        String tipoSugerido = propuesta.getTipoSugerido();
        if (tipoSugerido == null || tipoSugerido.isBlank()) {
            throw new ConflictException("El Tipo sugerido no puede estar vacío."); // <-- 409
        }
        Tipo tipoFinal = tipoRepository.findByNombre(tipoSugerido)
                .orElseGet(() -> tipoRepository.save(new Tipo(tipoSugerido)));

        // 4b. Traducir los GÉNEROS (409 si es nulo/vacío/inválido)
        Set<Genero> generosFinales = new HashSet<>();
        String generosSugeridosString = propuesta.getGenerosSugeridos();
        if (generosSugeridosString == null || generosSugeridosString.isBlank()) {
            throw new ConflictException("Los Géneros sugeridos no pueden estar vacíos."); // <-- 409
        }
        
        // ... (lógica de split y búsqueda/creación)
        String[] generosSugeridosArray = generosSugeridosString.split("\\s*,\\s*");
        
        for (String nombreGenero : generosSugeridosArray) {
            if (nombreGenero.isBlank()) continue;
            
            Genero genero = generoRepository.findByNombre(nombreGenero)
                    .orElseGet(() -> generoRepository.save(new Genero(nombreGenero)));
            generosFinales.add(genero);
        }
        
        if (generosFinales.isEmpty()) {
             throw new ConflictException("Se debe proporcionar al menos un género válido."); // <-- 409
        }
        // ... (resto del método sin cambios estructurales)
        
        // 5. Crear el nuevo Elemento
        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(propuesta.getTituloSugerido());
        nuevoElemento.setDescripcion(propuesta.getDescripcionSugerida());
        nuevoElemento.setCreador(propuesta.getProponente());
        nuevoElemento.setTipo(tipoFinal);
        nuevoElemento.setGeneros(generosFinales);
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);

        // 6. Actualizar la propuesta como "APROBADA"
        propuesta.setEstadoPropuesta(EstadoPropuesta.APROBADO);
        propuesta.setRevisor(revisor);
        propuestaRepo.save(propuesta);

        // 7. Guardar el nuevo elemento
        Elemento elementoGuardado = elementoRepo.save(nuevoElemento);
        
        // 8. Devolver el DTO de Respuesta
        return new ElementoResponseDTO(elementoGuardado);
    }
}