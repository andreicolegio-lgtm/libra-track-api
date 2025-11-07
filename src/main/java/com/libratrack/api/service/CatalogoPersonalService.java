package com.libratrack.api.service;

import com.libratrack.api.dto.CatalogoPersonalResponseDTO;
import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.CatalogoPersonalRepository;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importado

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la lógica de negocio del catálogo personal (RF05-RF08).
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
        // Asumimos que el repositorio tiene este método
        List<CatalogoPersonal> catalogo = catalogoRepo.findByUsuario_Username(username);
        
        return catalogo.stream()
                .map(CatalogoPersonalResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Añade un elemento al catálogo personal de un usuario (RF05).
     */
    @Transactional // Añadido por seguridad
    public CatalogoPersonalResponseDTO addElementoAlCatalogo(String username, Long elementoId) throws Exception {
        
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado."));
        Elemento elemento = elementoRepo.findById(elementoId)
                .orElseThrow(() -> new Exception("Elemento no encontrado."));

        if (catalogoRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId()).isPresent()) {
            throw new Exception("Este elemento ya está en tu catálogo.");
        }

        CatalogoPersonal nuevaEntrada = new CatalogoPersonal();
        nuevaEntrada.setUsuario(usuario);
        nuevaEntrada.setElemento(elemento);
        // El estado y progreso por defecto se asignan en la entidad

        CatalogoPersonal entradaGuardada = catalogoRepo.save(nuevaEntrada);
        
        return new CatalogoPersonalResponseDTO(entradaGuardada);
    }

    /**
     * Actualiza el estado y/o el progreso de un elemento en el catálogo (RF06, RF07).
     *
     * CORREGIDO: Este método ahora usa getTemporadaActual() y getUnidadActual()
     * en lugar del obsoleto getProgresoEspecifico().
     */
    @Transactional
    public CatalogoPersonalResponseDTO updateEntradaCatalogo(String username, Long elementoId, CatalogoUpdateDTO dto) throws Exception {
        
        // 1. Buscar la entrada específica que se quiere actualizar
        CatalogoPersonal entrada = catalogoRepo.findByUsuario_UsernameAndElemento_Id(username, elementoId)
                .orElseThrow(() -> new Exception("Este elemento no está en tu catálogo."));

        // 2. Actualizar los campos
        if (dto.getEstadoPersonal() != null) {
            entrada.setEstadoPersonal(dto.getEstadoPersonal()); // RF06
        }
        
        // --- LÓGICA DE PROGRESO DETALLADO (Punto 6) ---
        // CORREGIDO: Usa los getters del DTO de 110-MMMMMM
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
    @Transactional // Añadido por seguridad
    public void removeElementoDelCatalogo(String username, Long elementoId) throws Exception {
        
        CatalogoPersonal entrada = catalogoRepo.findByUsuario_UsernameAndElemento_Id(username, elementoId)
                .orElseThrow(() -> new Exception("Este elemento no está en tu catálogo."));
        
        catalogoRepo.delete(entrada);
    }
}