package com.libratrack.api.service;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
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
     * Obtiene todas las reseñas de un elemento específico (RF12)
     * y las mapea a DTOs para la respuesta.
     * @param elementoId El ID del elemento.
     * @return Lista de DTOs de reseñas.
     */
    public List<ResenaResponseDTO> getResenasByElementoId(Long elementoId) {
        // 1. Buscamos las entidades en la base de datos
        List<Resena> resenas = resenaRepo.findByElementoIdOrderByFechaCreacionDesc(elementoId);

        // 2. Mapeamos (convertimos) la List<Resena> a List<ResenaResponseDTO>
        // Usamos el constructor que creamos en el DTO.
        return resenas.stream()
                .map(ResenaResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva reseña (RF12).
     * @param dto El DTO con los datos de la reseña.
     * @return La nueva reseña creada.
     */
    public ResenaResponseDTO createResena(ResenaDTO dto) throws Exception {
        // 1. Verificar si el usuario ya ha reseñado este elemento
        Optional<Resena> existingResena = resenaRepo.findByUsuarioIdAndElementoId(dto.getUsuarioId(), dto.getElementoId());
        if (existingResena.isPresent()) {
            throw new Exception("Ya has reseñado este elemento.");
        }

        // 2. Verificar que el usuario y el elemento existen
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new Exception("Usuario no encontrado."));
        Elemento elemento = elementoRepo.findById(dto.getElementoId())
                .orElseThrow(() -> new Exception("Elemento no encontrado."));

        // 3. Crear la nueva entidad Resena
        Resena nuevaResena = new Resena();
        nuevaResena.setUsuario(usuario);
        nuevaResena.setElemento(elemento);
        
        // 4. Validar y asignar los datos de la reseña
        if (dto.getValoracion() == null || dto.getValoracion() < 1 || dto.getValoracion() > 5) {
             throw new Exception("La valoración debe estar entre 1 y 5.");
        }
        nuevaResena.setValoracion(dto.getValoracion());
        nuevaResena.setTextoResena(dto.getTextoResena());
        // La fecha se asigna automáticamente con @PrePersist

        Resena resenaGuardada = resenaRepo.save(nuevaResena);
        return new ResenaResponseDTO(resenaGuardada); // Devuelve el DTO de respuesta
    }
}