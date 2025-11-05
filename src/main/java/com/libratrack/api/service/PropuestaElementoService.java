package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoResponseDTO; // DTO para enviar el Elemento (evita error 500)
import com.libratrack.api.dto.PropuestaRequestDTO; // DTO para recibir la propuesta
import com.libratrack.api.dto.PropuestaResponseDTO; // DTO para enviar la propuesta (evita error 500)
import com.libratrack.api.entity.*; // Importa todas las entidades
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPropuesta;
import com.libratrack.api.repository.*; // Importa todos los repositorios
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ¡Muy importante!

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para la lógica de negocio de la cola de moderación.
 * Gestiona el ciclo de vida de las propuestas de los usuarios.
 * Implementa RF13, RF14, RF15, RF16.
 */
@Service
public class PropuestaElementoService {

    // --- Inyección de Dependencias ---
    // Este servicio coordina 5 repositorios para funcionar.
    
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
     * Esta es la "sala de espera".
     *
     * @param dto El DTO (PropuestaRequestDTO) con los datos sugeridos.
     * @param proponenteUsername El 'username' del usuario (extraído del token JWT) que hace la propuesta.
     * @return El DTO (PropuestaResponseDTO) de la PropuestaElemento guardada.
     * @throws Exception Si el usuario proponente no se encuentra.
     */
    public PropuestaResponseDTO createPropuesta(PropuestaRequestDTO dto, String proponenteUsername) throws Exception {
        // 1. Buscar al usuario proponente por su nombre (del token)
        Usuario proponente = usuarioRepo.findByUsername(proponenteUsername)
                .orElseThrow(() -> new Exception("Usuario proponente no encontrado."));

        // 2. Crear la nueva entidad Propuesta
        PropuestaElemento nuevaPropuesta = new PropuestaElemento();
        nuevaPropuesta.setProponente(proponente);
        nuevaPropuesta.setTituloSugerido(dto.getTituloSugerido());
        nuevaPropuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
        nuevaPropuesta.setTipoSugerido(dto.getTipoSugerido());
        nuevaPropuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
        
        // El estado por defecto es 'PENDIENTE' (definido en la entidad)

        // 3. Guardar en la tabla 'propuestas_elementos'
        PropuestaElemento propuestaGuardada = propuestaRepo.save(nuevaPropuesta);
        
        // 4. Devolver el DTO de Respuesta (Buena práctica, evita LazyException)
        return new PropuestaResponseDTO(propuestaGuardada);
    }

    /**
     * Obtiene la lista de propuestas pendientes (RF14 - Panel de Moderación).
     *
     * @return Lista de DTOs de propuestas con estado PENDIENTE.
     */
    public List<PropuestaResponseDTO> getPropuestasPendientes() {
        // 1. Busca las entidades 'PropuestaElemento'
        List<PropuestaElemento> propuestas = propuestaRepo.findByEstadoPropuesta(EstadoPropuesta.PENDIENTE);
        
        // 2. Mapea la lista de Entidades a una lista de DTOs
        // (Soluciona el error 500/403 que tuvimos al probar)
        return propuestas.stream()
                .map(PropuestaResponseDTO::new)
                .collect(Collectors.toList());
    }


    /**
     * Aprueba una propuesta (RF15).
     * Esta es la lógica más compleja: "traduce" los strings de la propuesta,
     * busca o crea las entidades Tipo/Genero, y copia los datos a la tabla 'elementos'.
     *
     * @param propuestaId El ID de la propuesta a aprobar.
     * @param revisorId El ID del moderador (del token JWT) que aprueba.
     * @return El DTO (ElementoResponseDTO) del nuevo Elemento creado.
     * @throws Exception Si la propuesta no existe, ya fue gestionada, o faltan datos.
     */
    @Transactional // Anotación crucial: si algo falla (ej. el género está vacío),
                   // revierte TODOS los cambios en la BD (ej. no guarda el Tipo nuevo).
                   // Mantiene la BD limpia.
    public ElementoResponseDTO aprobarPropuesta(Long propuestaId, Long revisorId) throws Exception {
        
        // 1. Buscar al moderador (revisor)
        Usuario revisor = usuarioRepo.findById(revisorId)
                .orElseThrow(() -> new Exception("Usuario revisor no encontrado."));
        // (En una V2, validaríamos que revisor.getEsModerador() == true)

        // 2. Buscar la propuesta
        PropuestaElemento propuesta = propuestaRepo.findById(propuestaId)
                .orElseThrow(() -> new Exception("Propuesta no encontrada."));

        // 3. Validación de estado
        if (propuesta.getEstadoPropuesta() != EstadoPropuesta.PENDIENTE) {
            throw new Exception("Esta propuesta ya ha sido gestionada.");
        }

        // 4. --- INICIO DE LA LÓGICA DE "TRADUCCIÓN" (RF15) ---

        // 4a. Traducir el TIPO (Buscar o Crear)
        String tipoSugerido = propuesta.getTipoSugerido();
        if (tipoSugerido == null || tipoSugerido.isBlank()) {
            throw new Exception("El Tipo sugerido no puede estar vacío.");
        }
        // Busca el Tipo por nombre; si no existe (.orElseGet), lo crea y lo guarda.
        Tipo tipoFinal = tipoRepository.findByNombre(tipoSugerido)
                .orElseGet(() -> tipoRepository.save(new Tipo(tipoSugerido)));

        // 4b. Traducir los GÉNEROS (Buscar o Crear)
        Set<Genero> generosFinales = new HashSet<>();
        String generosSugeridosString = propuesta.getGenerosSugeridos();
        if (generosSugeridosString == null || generosSugeridosString.isBlank()) {
            throw new Exception("Los Géneros sugeridos no pueden estar vacíos.");
        }
        
        // Separa el string "Aventura, Fantasía" en un array ["Aventura", "Fantasía"]
        String[] generosSugeridosArray = generosSugeridosString.split("\\s*,\\s*"); // Separa por comas
        
        for (String nombreGenero : generosSugeridosArray) {
            if (nombreGenero.isBlank()) continue; // Ignora géneros vacíos si hay comas extra
            
            // Busca el Genero por nombre; si no existe (.orElseGet), lo crea.
            Genero genero = generoRepository.findByNombre(nombreGenero)
                    .orElseGet(() -> generoRepository.save(new Genero(nombreGenero)));
            generosFinales.add(genero);
        }
        
        if (generosFinales.isEmpty()) {
             throw new Exception("Se debe proporcionar al menos un género válido.");
        }

        // --- FIN DE LA LÓGICA DE "TRADUCCIÓN" ---

        // 5. Crear el nuevo Elemento con los datos "traducidos"
        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(propuesta.getTituloSugerido());
        nuevoElemento.setDescripcion(propuesta.getDescripcionSugerida());
        nuevoElemento.setCreador(propuesta.getProponente()); // Asignamos el proponente original (RF13)
        nuevoElemento.setTipo(tipoFinal);
        nuevoElemento.setGeneros(generosFinales);
        
        // (RF16) El contenido aprobado se marca como COMUNITARIO por defecto
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);
        // (En una V2, un Admin podría pasar un flag para marcarlo como OFICIAL)

        // 6. Actualizar la propuesta como "APROBADA" (RF15)
        propuesta.setEstadoPropuesta(EstadoPropuesta.APROBADO);
        propuesta.setRevisor(revisor);
        propuestaRepo.save(propuesta); // Se actualiza la fila en 'propuestas_elementos'

        // 7. Guardar el nuevo elemento en la tabla principal 'elementos'
        Elemento elementoGuardado = elementoRepo.save(nuevoElemento);
        
        // 8. Devolver el DTO de Respuesta (soluciona el error 500/403)
        return new ElementoResponseDTO(elementoGuardado);
    }
    
    // (En una V2, añadiríamos el método 'rechazarPropuesta(Long propuestaId, Long revisorId, String motivo)')
}