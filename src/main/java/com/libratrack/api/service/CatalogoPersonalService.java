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

import java.util.List;
import java.util.stream.Collectors;

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
     * @param usuarioId El ID del usuario.
     * @return Lista de sus entradas de catálogo.
     */
    public List<CatalogoPersonalResponseDTO> getCatalogoByUsuarioId(Long usuarioId) {
        List<CatalogoPersonal> catalogo = catalogoRepo.findByUsuarioId(usuarioId);
        // Mapeamos la lista de Entidades a una lista de DTOs
        return catalogo.stream()
            .map(CatalogoPersonalResponseDTO::new)
            .collect(Collectors.toList());
    }

    /**
     * Añade un elemento al catálogo personal de un usuario (RF05).
     * @param usuarioId El ID del usuario.
     * @param elementoId El ID del elemento a añadir.
     * @return La nueva entrada del catálogo creada.
     */
    public CatalogoPersonalResponseDTO addElementoAlCatalogo(Long usuarioId, Long elementoId) throws Exception {
        // ... (toda la lógica de verificación de 'if/else' y 'try/catch' se queda igual) ...
        if (catalogoRepo.findByUsuarioIdAndElementoId(usuarioId, elementoId).isPresent()) {
            throw new Exception("Este elemento ya está en tu catálogo.");
        }
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado."));
        Elemento elemento = elementoRepo.findById(elementoId)
                .orElseThrow(() -> new Exception("Elemento no encontrado."));

        CatalogoPersonal nuevaEntrada = new CatalogoPersonal();
        nuevaEntrada.setUsuario(usuario);
        nuevaEntrada.setElemento(elemento);

        CatalogoPersonal entradaGuardada = catalogoRepo.save(nuevaEntrada);

        // Convertimos la Entidad guardada a un DTO para la respuesta
        return new CatalogoPersonalResponseDTO(entradaGuardada);
    }

    /**
     * Actualiza el estado y/o el progreso de un elemento en el catálogo (RF06, RF07).
     * @param usuarioId El ID del usuario.
     * @param elementoId El ID del elemento.
     * @param dto El DTO con los datos a actualizar.
     * @return La entrada del catálogo actualizada.
     */
    public CatalogoPersonalResponseDTO updateEntradaCatalogo(Long usuarioId, Long elementoId, CatalogoUpdateDTO dto) throws Exception {
        CatalogoPersonal entrada = catalogoRepo.findByUsuarioIdAndElementoId(usuarioId, elementoId)
                .orElseThrow(() -> new Exception("Este elemento no está en tu catálogo."));

        if (dto.getEstadoPersonal() != null) {
            entrada.setEstadoPersonal(dto.getEstadoPersonal());
        }
        if (dto.getProgresoEspecifico() != null) {
            entrada.setProgresoEspecifico(dto.getProgresoEspecifico());
        }

        CatalogoPersonal entradaGuardada = catalogoRepo.save(entrada);

        // Convertimos la Entidad actualizada a un DTO para la respuesta
        return new CatalogoPersonalResponseDTO(entradaGuardada);
    }

    /**
     * Elimina un elemento del catálogo personal de un usuario.
     * @param usuarioId El ID del usuario.
     * @param elementoId El ID del elemento.
     */
    public void removeElementoDelCatalogo(Long usuarioId, Long elementoId) throws Exception {
        // 1. Buscar la entrada
        CatalogoPersonal entrada = catalogoRepo.findByUsuarioIdAndElementoId(usuarioId, elementoId)
                .orElseThrow(() -> new Exception("Este elemento no está en tu catálogo."));
        
        // 2. Borrarla
        catalogoRepo.delete(entrada);
    }
}