package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoDTO; // DTO para recibir datos de creación
import com.libratrack.api.dto.ElementoResponseDTO; // DTO para enviar datos (evita error 500)
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.model.EstadoContenido; // Enum para 'OFICIAL'/'COMUNITARIO'
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
import java.util.stream.Collectors; // Para el mapeo de DTOs

/**
 * Servicio para la lógica de negocio relacionada con la entidad Elemento.
 * Gestiona la creación y recuperación de los elementos del catálogo principal.
 * Implementa RF09, RF10, RF13, RF15.
 */
@Service
public class ElementoService {

    // --- Inyección de Dependencias ---
    // Este servicio es complejo y necesita coordinar 4 repositorios.
    
    @Autowired
    private ElementoRepository elementoRepository;
    
    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    /**
     * Lógica de negocio para crear un nuevo Elemento a partir de un DTO.
     * Este método es llamado por el PropuestaElementoService durante la aprobación (RF15)
     * y también podría ser llamado by un AdminController si un Admin
     * quisiera crear un elemento directamente (bypassando la cola de propuestas).
     *
     * @param dto El DTO (ElementoDTO) con los datos de la petición.
     * @return El ElementoResponseDTO del elemento que se ha guardado.
     * @throws Exception Si no se encuentra el Tipo, Genero o Usuario.
     */
    @Transactional // Asegura que si algo falla (ej. un Genero ID no existe), no se guarde nada.
    public ElementoResponseDTO createElemento(ElementoDTO dto) throws Exception {
        
        // 1. "Traducir" los IDs del DTO a Entidades reales de la BD
        
        Tipo tipo = tipoRepository.findById(dto.getTipoId())
                .orElseThrow(() -> new Exception("Tipo no encontrado con id: " + dto.getTipoId()));

        Usuario creador = usuarioRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new Exception("Usuario creador no encontrado con id: " + dto.getCreadorId()));
        
        // Busca todos los IDs de géneros en la BD
        Set<Genero> generos = new HashSet<>(generoRepository.findAllById(dto.getGeneroIds()));
        if (generos.size() != dto.getGeneroIds().size() || generos.isEmpty()) {
            throw new Exception("Uno o más IDs de Género no son válidos o la lista está vacía.");
        }

        // 2. Mapear los datos del DTO a la nueva entidad Elemento
        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(dto.getTitulo());
        nuevoElemento.setDescripcion(dto.getDescripcion());
        nuevoElemento.setFechaLanzamiento(dto.getFechaLanzamiento());
        nuevoElemento.setImagenPortadaUrl(dto.getImagenPortadaUrl());
        
        // 3. Establecer las relaciones con las entidades encontradas
        nuevoElemento.setTipo(tipo);
        nuevoElemento.setGeneros(generos);
        nuevoElemento.setCreador(creador); // Asigna el proponente (RF13)
        
        // 4. Establecer el estado (RF16)
        // Por defecto, un elemento creado así es COMUNITARIO.
        // (Un Admin podría cambiar esto después).
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);

        // 5. Guardar el nuevo elemento en la base de datos
        Elemento elementoGuardado = elementoRepository.save(nuevoElemento);
        
        // 6. Devolver el DTO de Respuesta (¡Mejor Práctica!)
        // Nunca devolvemos la Entidad directamente a un controlador para evitar
        // errores de LazyInitializationException (el error 500 que tuvimos).
        return new ElementoResponseDTO(elementoGuardado);
    }

    /**
     * Busca todos los elementos en la base de datos (RF09 - Búsqueda Global).
     *
     * @return Una lista de DTOs de todos los Elementos.
     */
    public List<ElementoResponseDTO> findAllElementos() {
        List<Elemento> elementos = elementoRepository.findAll();
        
        // Mapea la lista de Entidades (Elemento) a una lista de DTOs (ElementoResponseDTO)
        // Esto evita el error LazyInitializationException.
        return elementos.stream()
                .map(ElementoResponseDTO::new) // Usa el constructor del DTO
                .collect(Collectors.toList());
    }

    /**
     * Busca un elemento por su ID (RF10 - Ficha Detallada).
     *
     * @param id El ID del elemento a buscar.
     * @return Un Optional que contendrá el DTO del Elemento si existe.
     */
    public Optional<ElementoResponseDTO> findElementoById(Long id) {
        // Busca la entidad por su ID
        Optional<Elemento> elementoOptional = elementoRepository.findById(id);

        // Si la entidad existe (isPresent), la mapea al DTO de respuesta.
        // Si no, devuelve un Optional vacío.
        return elementoOptional.map(ElementoResponseDTO::new);
    }
}