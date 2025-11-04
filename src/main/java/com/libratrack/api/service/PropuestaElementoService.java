package com.libratrack.api.service;

import com.libratrack.api.dto.PropuestaRequestDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPropuesta;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.PropuestaElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ¡Importante!

import java.util.List;

@Service
public class PropuestaElementoService {

    @Autowired
    private PropuestaElementoRepository propuestaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ElementoRepository elementoRepo;

    // (Más adelante inyectaremos TipoRepo y GeneroRepo para la aprobación)

    /**
     * Crea una nueva propuesta y la añade a la cola de moderación (RF13).
     *
     * @param dto El DTO con los datos sugeridos.
     * @param proponenteId El ID del usuario (extraído del token JWT) que hace la propuesta.
     * @return La PropuestaElemento guardada.
     */
    public PropuestaElemento createPropuesta(PropuestaRequestDTO dto, Long proponenteId) throws Exception {
        // 1. Buscar al usuario proponente
        Usuario proponente = usuarioRepo.findById(proponenteId)
                .orElseThrow(() -> new Exception("Usuario no encontrado."));

        // 2. Crear la nueva entidad Propuesta
        PropuestaElemento nuevaPropuesta = new PropuestaElemento();
        nuevaPropuesta.setProponente(proponente);
        nuevaPropuesta.setTituloSugerido(dto.getTituloSugerido());
        nuevaPropuesta.setDescripcionSugerida(dto.getDescripcionSugerida());
        nuevaPropuesta.setTipoSugerido(dto.getTipoSugerido());
        nuevaPropuesta.setGenerosSugeridos(dto.getGenerosSugeridos());
        
        // El estado por defecto es PENDIENTE (definido en la entidad)

        // 3. Guardar en la tabla de "sala de espera"
        return propuestaRepo.save(nuevaPropuesta);
    }

    /**
     * Obtiene la lista de propuestas pendientes (RF14 - Panel de Moderación).
     * @return Lista de propuestas con estado PENDIENTE.
     */
    public List<PropuestaElemento> getPropuestasPendientes() {
        return propuestaRepo.findByEstadoPropuesta(EstadoPropuesta.PENDIENTE);
    }


    /**
     * Aprueba una propuesta (RF15).
     * Esto copia los datos de la propuesta a la tabla 'elementos'.
     *
     * @param propuestaId El ID de la propuesta a aprobar.
     * @param revisorId El ID del moderador (del token JWT) que aprueba.
     * @return El nuevo Elemento creado.
     */
    @Transactional // Esta anotación asegura que si algo falla, no se guarde nada (evita corrupción)
    public Elemento aprobarPropuesta(Long propuestaId, Long revisorId) throws Exception {
        
        // 1. Buscar al moderador (revisor)
        Usuario revisor = usuarioRepo.findById(revisorId)
                .orElseThrow(() -> new Exception("Usuario revisor no encontrado."));
        // (Aquí podríamos verificar si el revisor tiene rol de MODERADOR)

        // 2. Buscar la propuesta
        PropuestaElemento propuesta = propuestaRepo.findById(propuestaId)
                .orElseThrow(() -> new Exception("Propuesta no encontrada."));

        if (propuesta.getEstadoPropuesta() != EstadoPropuesta.PENDIENTE) {
            throw new Exception("Esta propuesta ya ha sido gestionada.");
        }

        // 3. Crear el nuevo Elemento con los datos de la propuesta
        // (¡Aquí es donde necesitamos la lógica para buscar Tipo y Genero!)
        // --- INICIO DE LÓGICA COMPLEJA (Simplificada por ahora) ---
        
        // TO DO: Necesitamos buscar Tipo y Genero por los strings.
        // Por ahora, asumimos que se copian los datos y se asigna un Tipo y Genero por defecto.
        // Esto lo arreglaremos después.
        
        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(propuesta.getTituloSugerido());
        nuevoElemento.setDescripcion(propuesta.getDescripcionSugerida());
        
        // Asignamos el proponente original
        nuevoElemento.setCreador(propuesta.getProponente());
        
        // (RF16) El contenido aprobado por un moderador se marca como COMUNITARIO
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);
        
        // --- FIN DE LÓGICA COMPLEJA ---
        
        // 4. Actualizar la propuesta como "APROBADA"
        propuesta.setEstadoPropuesta(EstadoPropuesta.APROBADO);
        propuesta.setRevisor(revisor);
        propuestaRepo.save(propuesta); // Guardamos el cambio en la propuesta

        // 5. Guardar el nuevo elemento en la tabla principal
        // (Devolvemos el elemento creado)
        // **ERROR**: ¡Aún no hemos asignado Tipo y Generos! (Lo haremos en el siguiente paso)
        
        // --- Temporalmente, guardaremos el elemento incompleto ---
        // return elementoRepo.save(nuevoElemento);
        
        // --- Por ahora, solo devolvemos null para evitar errores ---
        return null; // <-- ARREGLAREMOS ESTO
    }
    
    // (Añadiremos rechazarPropuesta() más tarde)
}