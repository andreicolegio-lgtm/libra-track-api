package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoDTO;
import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.GeneroRepository;
import com.libratrack.api.repository.TipoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para la lógica de negocio relacionada con la entidad Elemento.
 */
@Service
public class ElementoService {
    
    @Autowired
    private ElementoRepository elementoRepository;
    
    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Lógica de negocio para crear un nuevo Elemento (RF15).
     * CORREGIDO: Se usan los setters correctos 'setUrlImagen' y 'setFechaLanzamiento'.
     */
    @Transactional
    public ElementoResponseDTO createElemento(ElementoDTO dto) throws Exception {
        
        // 1. "Traducir" IDs a Entidades
        Tipo tipo = tipoRepository.findById(dto.getTipoId())
                .orElseThrow(() -> new Exception("Tipo no encontrado con id: " + dto.getTipoId()));

        Usuario creador = usuarioRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new Exception("Usuario creador no encontrado con id: " + dto.getCreadorId()));
        
        Set<Genero> generos = new HashSet<>(generoRepository.findAllById(dto.getGeneroIds()));
        if (generos.size() != dto.getGeneroIds().size() || generos.isEmpty()) {
            throw new Exception("Uno o más IDs de Género no son válidos o la lista está vacía.");
        }

        // 2. Mapear DTO a la nueva entidad
        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(dto.getTitulo());
        nuevoElemento.setDescripcion(dto.getDescripcion());
        
        // CORREGIDO: Usar los getters del DTO (110-NNNNNN)
        nuevoElemento.setFechaLanzamiento(dto.getFechaLanzamiento()); 
        nuevoElemento.setUrlImagen(dto.getUrlImagen()); // <-- CORREGIDO
        
        // 3. Establecer relaciones
        nuevoElemento.setTipo(tipo);
        nuevoElemento.setGeneros(generos);
        nuevoElemento.setCreador(creador); 
        
        // 4. Establecer estado (RF16)
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);
        
        // 5. Establecer estado de publicación (Punto 11)
        nuevoElemento.setEstadoPublicacion(EstadoPublicacion.DISPONIBLE); 

        // 6. Guardar
        Elemento elementoGuardado = elementoRepository.save(nuevoElemento);
        
        return new ElementoResponseDTO(elementoGuardado);
    }

    /**
     * Busca todos los elementos o filtra por 3 criterios (RF09).
     */
    public List<ElementoResponseDTO> findAllElementos(String searchText, String tipoName, String generoName) {
        
        List<Elemento> elementos = elementoRepository.findAll();
        if (elementos.isEmpty()) {
             return List.of();
        }
        
        List<Elemento> elementosFiltrados = elementos.stream()
            
            // Filtro 1: Búsqueda por Título
            .filter(e -> {
                if (searchText != null && !searchText.isBlank()) {
                    return e.getTitulo().toLowerCase().contains(searchText.toLowerCase());
                }
                return true;
            })
            
            // Filtro 2: Por Tipo
            .filter(e -> {
                if (tipoName != null && !tipoName.isBlank() && e.getTipo() != null) {
                    return e.getTipo().getNombre().equalsIgnoreCase(tipoName);
                }
                return true;
            })
            
            // Filtro 3: Por Género
            .filter(e -> {
                if (generoName != null && !generoName.isBlank() && e.getGeneros() != null) {
                    return e.getGeneros().stream()
                            .anyMatch(g -> g.getNombre().equalsIgnoreCase(generoName));
                }
                return true;
            })
            
            .collect(Collectors.toList());

        return elementosFiltrados.stream()
                .map(ElementoResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca un elemento por su ID (RF10 - Ficha Detallada).
     */
    public Optional<ElementoResponseDTO> findElementoById(Long id) {
        Optional<Elemento> elementoOptional = elementoRepository.findById(id);
        return elementoOptional.map(ElementoResponseDTO::new);
    }
}