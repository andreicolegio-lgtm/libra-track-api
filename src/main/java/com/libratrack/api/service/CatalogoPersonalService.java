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

/**
 * (Refactorizado por Seguridad)
 * Servicio para la lógica de negocio del catálogo personal.
 * Todos los métodos ahora aceptan un 'username' (del token JWT)
 * en lugar de un 'usuarioId' (inseguro), para asegurar que
 * un usuario solo pueda modificar su propio catálogo.
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
     *
     * @param username El 'username' del usuario (obtenido del token).
     * @return Una lista de DTOs de las entradas del catálogo de ese usuario.
     */
    public List<CatalogoPersonalResponseDTO> getCatalogoByUsername(String username) {
        // 1. Busca las entidades en la base de datos
        // (El repositorio debe ser actualizado para buscar por username)
        List<CatalogoPersonal> catalogo = catalogoRepo.findByUsuario_Username(username);
        
        // 2. Mapea la lista de Entidades a una lista de DTOs
        return catalogo.stream()
                .map(CatalogoPersonalResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Añade un elemento al catálogo personal de un usuario (RF05).
     *
     * @param username El 'username' del usuario (del token JWT).
     * @param elementoId El ID del elemento a añadir (de la URL).
     * @return El DTO de la nueva entrada del catálogo creada.
     */
    public CatalogoPersonalResponseDTO addElementoAlCatalogo(String username, Long elementoId) throws Exception {
        
        // 1. Verificación de Entidades:
        // Buscamos al usuario por 'username' para obtener la entidad completa
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado."));
        Elemento elemento = elementoRepo.findById(elementoId)
                .orElseThrow(() -> new Exception("Elemento no encontrado."));

        // 2. Validación: Verificar si el elemento ya está en el catálogo
        // (Usamos los IDs de las entidades que encontramos)
        if (catalogoRepo.findByUsuarioIdAndElementoId(usuario.getId(), elemento.getId()).isPresent()) {
            throw new Exception("Este elemento ya está en tu catálogo.");
        }

        // 3. Crear la nueva entrada del catálogo
        CatalogoPersonal nuevaEntrada = new CatalogoPersonal();
        nuevaEntrada.setUsuario(usuario);
        nuevaEntrada.setElemento(elemento);

        CatalogoPersonal entradaGuardada = catalogoRepo.save(nuevaEntrada);
        
        return new CatalogoPersonalResponseDTO(entradaGuardada);
    }

    /**
     * Actualiza el estado y/o el progreso de un elemento en el catálogo (RF06, RF07).
     *
     * @param username El 'username' del usuario (del token JWT).
     * @param elementoId El ID del elemento (de la URL).
     * @param dto El DTO (CatalogoUpdateDTO) con los datos a actualizar.
     * @return El DTO de la entrada del catálogo ya actualizada.
     */
    public CatalogoPersonalResponseDTO updateEntradaCatalogo(String username, Long elementoId, CatalogoUpdateDTO dto) throws Exception {
        
        // 1. Buscar la entrada específica que se quiere actualizar
        // (Usamos el repositorio para buscar por username y elementoId)
        CatalogoPersonal entrada = catalogoRepo.findByUsuario_UsernameAndElemento_Id(username, elementoId)
                .orElseThrow(() -> new Exception("Este elemento no está en tu catálogo."));

        // 2. Actualizar los campos
        if (dto.getEstadoPersonal() != null) {
            entrada.setEstadoPersonal(dto.getEstadoPersonal()); // RF06
        }
        if (dto.getProgresoEspecifico() != null) {
            entrada.setProgresoEspecifico(dto.getProgresoEspecifico()); // RF07
        }

        CatalogoPersonal entradaGuardada = catalogoRepo.save(entrada);
        
        return new CatalogoPersonalResponseDTO(entradaGuardada);
    }

    /**
     * Elimina un elemento del catálogo personal de un usuario.
     *
     * @param username El 'username' del usuario (del token JWT).
     * @param elementoId El ID del elemento (de la URL).
     */
    public void removeElementoDelCatalogo(String username, Long elementoId) throws Exception {
        
        // 1. Buscar la entrada que se quiere eliminar
        CatalogoPersonal entrada = catalogoRepo.findByUsuario_UsernameAndElemento_Id(username, elementoId)
                .orElseThrow(() -> new Exception("Este elemento no está en tu catálogo."));
        
        // 2. Borrarla
        catalogoRepo.delete(entrada);
    }
}