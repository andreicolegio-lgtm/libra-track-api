package com.libratrack.api.service;

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
    public List<CatalogoPersonal> getCatalogoByUsuarioId(Long usuarioId) {
        return catalogoRepo.findByUsuarioId(usuarioId);
    }

    /**
     * Añade un elemento al catálogo personal de un usuario (RF05).
     * @param usuarioId El ID del usuario.
     * @param elementoId El ID del elemento a añadir.
     * @return La nueva entrada del catálogo creada.
     */
    public CatalogoPersonal addElementoAlCatalogo(Long usuarioId, Long elementoId) throws Exception {
        // 1. Verificar si el elemento ya está en el catálogo
        if (catalogoRepo.findByUsuarioIdAndElementoId(usuarioId, elementoId).isPresent()) {
            throw new Exception("Este elemento ya está en tu catálogo.");
        }

        // 2. Verificar que el usuario y el elemento existen
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado."));
        Elemento elemento = elementoRepo.findById(elementoId)
                .orElseThrow(() -> new Exception("Elemento no encontrado."));

        // 3. Crear la nueva entrada del catálogo
        CatalogoPersonal nuevaEntrada = new CatalogoPersonal();
        nuevaEntrada.setUsuario(usuario);
        nuevaEntrada.setElemento(elemento);
        // El estado por defecto (PENDIENTE) se asigna automáticamente
        // gracias al @PrePersist y el valor por defecto en la Entidad.

        return catalogoRepo.save(nuevaEntrada);
    }

    /**
     * Actualiza el estado y/o el progreso de un elemento en el catálogo (RF06, RF07).
     * @param usuarioId El ID del usuario.
     * @param elementoId El ID del elemento.
     * @param dto El DTO con los datos a actualizar.
     * @return La entrada del catálogo actualizada.
     */
    public CatalogoPersonal updateEntradaCatalogo(Long usuarioId, Long elementoId, CatalogoUpdateDTO dto) throws Exception {
        // 1. Buscar la entrada específica que se quiere actualizar
        CatalogoPersonal entrada = catalogoRepo.findByUsuarioIdAndElementoId(usuarioId, elementoId)
                .orElseThrow(() -> new Exception("Este elemento no está en tu catálogo."));

        // 2. Actualizar los campos si vienen en el DTO
        if (dto.getEstadoPersonal() != null) {
            entrada.setEstadoPersonal(dto.getEstadoPersonal()); // RF06
        }
        
        if (dto.getProgresoEspecifico() != null) {
            entrada.setProgresoEspecifico(dto.getProgresoEspecifico()); // RF07
        }

        // 3. Guardar los cambios
        return catalogoRepo.save(entrada);
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