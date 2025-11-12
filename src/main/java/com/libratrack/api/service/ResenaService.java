// Archivo: src/main/java/com/libratrack/api/service/ResenaService.java
package com.libratrack.api.service;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Resena;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException; 
import com.libratrack.api.exception.ResourceNotFoundException; 
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.ResenaRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- ¡NUEVA IMPORTACIÓN!
import java.util.List; 
import java.util.Optional;
import java.util.stream.Collectors; 

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private ElementoRepository elementoRepo;

    /**
     * Obtiene todas las reseñas de un elemento específico (RF12).
     * --- ¡ACTUALIZADO! ---
     * Añadimos @Transactional para evitar LazyInitializationException
     * al acceder a la foto de perfil del usuario en el DTO.
     */
    @Transactional(readOnly = true) // <-- ¡LÍNEA AÑADIDA!
    public List<ResenaResponseDTO> getResenasByElementoId(Long elementoId) {
        if (!elementoRepo.existsById(elementoId)) {
            throw new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId); 
        }
        
        List<Resena> resenas = resenaRepo.findByElementoIdOrderByFechaCreacionDesc(elementoId);
        
        // Esto ahora es seguro gracias a @Transactional
        return resenas.stream()
                .map(ResenaResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva reseña (RF12).
     * (Este método ya era @Transactional implícitamente por 'save', 
     * pero lo hacemos explícito para claridad)
     */
    @Transactional
    public ResenaResponseDTO createResena(ResenaDTO dto, String username) { 
        
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Token de usuario inválido.")); 
        
        Elemento elemento = elementoRepo.findById(dto.getElementoId())
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + dto.getElementoId())); 

        Optional<Resena> existingResena = resenaRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId());
        if (existingResena.isPresent()) {
            throw new ConflictException("Ya has reseñado este elemento."); 
        }

        Resena nuevaResena = new Resena();
        nuevaResena.setUsuario(usuario);
        nuevaResena.setElemento(elemento);
        
        if (dto.getValoracion() == null || dto.getValoracion() < 1 || dto.getValoracion() > 5) {
             throw new ConflictException("La valoración debe estar entre 1 y 5."); 
        }
        nuevaResena.setValoracion(dto.getValoracion());
        nuevaResena.setTextoResena(dto.getTextoResena());

        Resena resenaGuardada = resenaRepo.save(nuevaResena);

        return new ResenaResponseDTO(resenaGuardada);
    }
}