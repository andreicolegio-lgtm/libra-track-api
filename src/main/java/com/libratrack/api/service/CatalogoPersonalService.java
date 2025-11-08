// Archivo: src/main/java/com/libratrack/api/service/CatalogoPersonalService.java
package com.libratrack.api.service;

import com.libratrack.api.dto.CatalogoPersonalResponseDTO;
import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException; // NUEVA IMPORTACIÓN
import com.libratrack.api.exception.ResourceNotFoundException; // NUEVA IMPORTACIÓN
import com.libratrack.api.repository.CatalogoPersonalRepository;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la lógica de negocio del catálogo personal (RF05-RF08).
 * REFACTORIZADO: Usa excepciones de negocio (ResourceNotFound/Conflict).
 */
@Service
public class CatalogoPersonalService {

    @Autowired
    private CatalogoPersonalRepository catalogoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private ElementoRepository elementoRepo;

    /**
     * Obtiene todas las entradas del catálogo de un usuario (RF08).
     */
    public List<CatalogoPersonalResponseDTO> getCatalogoByUsername(String username) {
        // ... (sin cambios, solo recuperación de datos)
        List<CatalogoPersonal> catalogo = catalogoRepo.findByUsuario_Username(username);
        
        return catalogo.stream()
                .map(CatalogoPersonalResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Añade un elemento al catálogo personal de un usuario (RF05).
     */
    @Transactional
    public CatalogoPersonalResponseDTO addElementoAlCatalogo(String username, Long elementoId) { // Eliminamos 'throws Exception'
        
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado.")); // <-- 404
        Elemento elemento = elementoRepo.findById(elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Elemento no encontrado con id: " + elementoId)); // <-- 404

        if (catalogoRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId()).isPresent()) {
            throw new ConflictException("Este elemento ya está en tu catálogo."); // <-- 409
        }

        CatalogoPersonal nuevaEntrada = new CatalogoPersonal();
        nuevaEntrada.setUsuario(usuario);
        nuevaEntrada.setElemento(elemento);

        CatalogoPersonal entradaGuardada = catalogoRepo.save(nuevaEntrada);
        
        return new CatalogoPersonalResponseDTO(entradaGuardada);
    }

    /**
     * Actualiza el estado y/o el progreso de un elemento en el catálogo (RF06, RF07).
     */
    @Transactional
    public CatalogoPersonalResponseDTO updateEntradaCatalogo(String username, Long elementoId, CatalogoUpdateDTO dto) { // Eliminamos 'throws Exception'
        
        // 1. Buscar la entrada específica que se quiere actualizar
        CatalogoPersonal entrada = catalogoRepo.findByUsuario_UsernameAndElemento_Id(username, elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Este elemento no está en tu catálogo (Elemento ID: " + elementoId + ").")); // <-- 404

        // 2. Actualizar los campos
        if (dto.getEstadoPersonal() != null) {
            entrada.setEstadoPersonal(dto.getEstadoPersonal());
        }
        
        if (dto.getTemporadaActual() != null) {
            entrada.setTemporadaActual(dto.getTemporadaActual());
        }
        
        if (dto.getUnidadActual() != null) {
            entrada.setUnidadActual(dto.getUnidadActual());
        }

        CatalogoPersonal entradaGuardada = catalogoRepo.save(entrada);
        
        return new CatalogoPersonalResponseDTO(entradaGuardada);
    }

    /**
     * Elimina un elemento del catálogo personal de un usuario.
     */
    @Transactional
    public void removeElementoDelCatalogo(String username, Long elementoId) { // Eliminamos 'throws Exception'
        
        CatalogoPersonal entrada = catalogoRepo.findByUsuario_UsernameAndElemento_Id(username, elementoId)
                .orElseThrow(() -> new ResourceNotFoundException("Este elemento no está en tu catálogo (Elemento ID: " + elementoId + ").")); // <-- 404
        
        catalogoRepo.delete(entrada);
    }
}