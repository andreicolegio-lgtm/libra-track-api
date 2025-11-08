// Archivo: src/main/java/com/libratrack/api/service/ResenaService.java
package com.libratrack.api.service;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Resena;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException; // NUEVA IMPORTACIÓN
import com.libratrack.api.exception.ResourceNotFoundException; // NUEVA IMPORTACIÓN
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.ResenaRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para la lógica de negocio relacionada con la entidad Resena (RF12).
 * REFACTORIZADO: Usa excepciones de negocio (ResourceNotFound/Conflict).
 */
@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ElementoRepository elementoRepo;

    // ... (getResenasByElementoId no requiere cambios)

    /**
     * Crea una nueva reseña (RF12).
     * @throws ResourceNotFoundException Si el usuario o elemento no existen (404).
     * @throws ConflictException Si el usuario ya ha reseñado este elemento o la valoración es inválida (409).
     */
    public ResenaResponseDTO createResena(ResenaDTO dto, String username) { // Se elimina 'throws Exception'
        
        // 1. Verificación de Entidades (¡Lanza 404 si no existe!)
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Token de usuario inválido.")); // <-- 404
        
        Elemento elemento = elementoRepo.findById(dto.getElementoId())
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + dto.getElementoId())); // <-- 404

        // 2. Validación: Ya ha reseñado (¡Lanza 409 si ya existe!)
        Optional<Resena> existingResena = resenaRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId());
        if (existingResena.isPresent()) {
            throw new ConflictException("Ya has reseñado este elemento."); // <-- 409
        }

        // 3. Crear la nueva entidad Resena
        Resena nuevaResena = new Resena();
        nuevaResena.setUsuario(usuario);
        nuevaResena.setElemento(elemento);
        
        // 4. Validación de Valoración (Doble capa de seguridad de negocio, lanza 409)
        if (dto.getValoracion() == null || dto.getValoracion() < 1 || dto.getValoracion() > 5) {
             throw new ConflictException("La valoración debe estar entre 1 y 5."); // <-- 409
        }
        nuevaResena.setValoracion(dto.getValoracion());
        nuevaResena.setTextoResena(dto.getTextoResena());

        // 5. Guardar la nueva reseña
        Resena resenaGuardada = resenaRepo.save(nuevaResena);

        // 6. Devolver el DTO de Respuesta
        return new ResenaResponseDTO(resenaGuardada);
    }
}