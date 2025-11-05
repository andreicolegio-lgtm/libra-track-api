package com.libratrack.api.service;

import com.libratrack.api.dto.ResenaDTO; // DTO para RECIBIR datos (crear)
import com.libratrack.api.dto.ResenaResponseDTO; // DTO para ENVIAR datos (evita error 500)
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Resena;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.ResenaRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Para el mapeo de DTOs

/**
 * (Refactorizado por Seguridad)
 * Servicio para la lógica de negocio relacionada con la entidad Resena (RF12).
 *
 * Se ha modificado 'createResena' para que acepte el 'username' del
 * usuario autenticado (del token JWT), en lugar de un 'usuarioId'
 * enviado por el cliente, previniendo suplantación de identidad.
 */
@Service
public class ResenaService {

    // --- Inyección de Dependencias ---
    @Autowired
    private ResenaRepository resenaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ElementoRepository elementoRepo;

    /**
     * Obtiene todas las reseñas de un elemento específico (RF12).
     *
     * @param elementoId El ID del elemento.
     * @return Una lista de DTOs de las reseñas de ese elemento.
     */
    public List<ResenaResponseDTO> getResenasByElementoId(Long elementoId) {
        
        // 1. Buscamos las entidades en la base de datos (ordenadas)
        List<Resena> resenas = resenaRepo.findByElementoIdOrderByFechaCreacionDesc(elementoId);

        // 2. Mapeamos (convertimos) la lista de Entidades a DTOs
        // (Soluciona la LazyInitializationException)
        return resenas.stream()
                .map(ResenaResponseDTO::new) 
                .collect(Collectors.toList());
    }

    /**
     * (Refactorizado) Crea una nueva reseña (RF12).
     *
     * @param dto El DTO (ResenaDTO) con los datos (elementoId, valoracion, texto).
     * @param username El 'username' del usuario (obtenido del token JWT).
     * @return El DTO (ResenaResponseDTO) de la reseña que se acaba de crear.
     * @throws Exception Si el usuario ya ha reseñado este elemento, o si el usuario/elemento no existen.
     */
    public ResenaResponseDTO createResena(ResenaDTO dto, String username) throws Exception {
        
        // 1. Verificación de Entidades (¡Usando 'username'!)
        // Buscamos al autor (Usuario) usando el 'username' del token
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new Exception("Token de usuario inválido."));
        
        // Buscamos el elemento que se está reseñando
        Elemento elemento = elementoRepo.findById(dto.getElementoId())
                .orElseThrow(() -> new Exception("Elemento no encontrado."));

        // 2. Validación: Verificar si el usuario ya ha reseñado este elemento
        // (Usamos los IDs de las entidades que encontramos)
        Optional<Resena> existingResena = resenaRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId());
        if (existingResena.isPresent()) {
            throw new Exception("Ya has reseñado este elemento.");
        }

        // 3. Crear la nueva entidad Resena
        Resena nuevaResena = new Resena();
        nuevaResena.setUsuario(usuario); // Asigna el autor
        nuevaResena.setElemento(elemento); // Asigna el elemento
        
        // 4. Validar y asignar los datos de la reseña
        if (dto.getValoracion() == null || dto.getValoracion() < 1 || dto.getValoracion() > 5) {
             throw new Exception("La valoración debe estar entre 1 y 5.");
        }
        nuevaResena.setValoracion(dto.getValoracion());
        nuevaResena.setTextoResena(dto.getTextoResena());
        // La fecha de creación (fechaCreacion) se asigna automáticamente con @PrePersist.

        // 5. Guardar la nueva reseña
        Resena resenaGuardada = resenaRepo.save(nuevaResena);

        // 6. Devolver el DTO de Respuesta
        return new ResenaResponseDTO(resenaGuardada);
    }
}