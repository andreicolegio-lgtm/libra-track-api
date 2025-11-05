package com.libratrack.api.controller;

import com.libratrack.api.dto.CatalogoPersonalResponseDTO;
import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.service.CatalogoPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import para seguridad
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // ¡Importante! Para obtener el usuario del token
import java.util.List;

/**
 * Controlador REST para el catálogo personal de un usuario.
 *
 * (Refactor de Seguridad):
 * Esta clase está diseñada para ser segura. NO acepta el ID del usuario
 * desde la URL. En su lugar, utiliza el objeto 'Principal' (inyectado
 * por Spring Security) para identificar al usuario basándose en el token JWT.
 * Esto garantiza que un usuario solo puede ver y modificar SU PROPIO catálogo.
 *
 * Implementa los endpoints para RF05, RF06, RF07, RF08.
 */
@RestController
@RequestMapping("/api/catalogo") // La ruta base (ya no incluye {usuarioId})
@PreAuthorize("hasAuthority('ROLE_USER')") // Requiere ser un usuario logueado
public class CatalogoPersonalController {

    @Autowired
    private CatalogoPersonalService catalogoService;

    /**
     * Endpoint para obtener el catálogo completo del usuario autenticado (RF08).
     * Escucha en: GET /api/catalogo
     *
     * @param principal Objeto inyectado por Spring Security que contiene el
     * token del usuario (y su 'username').
     * @return Lista de DTOs del catálogo del usuario.
     */
    @GetMapping
    public ResponseEntity<List<CatalogoPersonalResponseDTO>> getMiCatalogo(Principal principal) {
        // Obtenemos el 'username' del token (ej. "testuser")
        String username = principal.getName();
        
        List<CatalogoPersonalResponseDTO> catalogo = catalogoService.getCatalogoByUsername(username);
        return ResponseEntity.ok(catalogo);
    }

    /**
     * Endpoint para añadir un elemento al catálogo personal del usuario (RF05).
     * Escucha en: POST /api/catalogo/elementos/1
     * (Donde 1 es el elementoId)
     *
     * @param elementoId El ID del elemento a añadir (de la URL).
     * @param principal El usuario (del token JWT).
     * @return El DTO de la nueva entrada del catálogo creada.
     */
    @PostMapping("/elementos/{elementoId}")
    public ResponseEntity<?> addElementoAlCatalogo(
            @PathVariable Long elementoId,
            Principal principal) {
        
        try {
            // Pasamos el 'username' del token al servicio
            CatalogoPersonalResponseDTO nuevaEntrada = catalogoService.addElementoAlCatalogo(principal.getName(), elementoId);
            return new ResponseEntity<>(nuevaEntrada, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            // Error 409 si ya existe, Error 404 si el elemento no existe
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); 
        }
    }

    /**
     * Endpoint para actualizar el estado/progreso de un elemento (RF06, RF07).
     * Escucha en: PUT /api/catalogo/elementos/1
     *
     * @param elementoId El ID del elemento a actualizar (de la URL).
     * @param dto El DTO (CatalogoUpdateDTO) con los datos nuevos.
     * @param principal El usuario (del token JWT).
     * @return El DTO de la entrada del catálogo ya actualizada.
     */
    @PutMapping("/elementos/{elementoId}")
    public ResponseEntity<?> updateElementoDelCatalogo(
            @PathVariable Long elementoId,
            @RequestBody CatalogoUpdateDTO dto,
            Principal principal) {
        
        try {
            CatalogoPersonalResponseDTO entradaActualizada = catalogoService.updateEntradaCatalogo(principal.getName(), elementoId, dto);
            return ResponseEntity.ok(entradaActualizada); // 200 OK
        } catch (Exception e) {
            // Error 404 si la entrada no existe en el catálogo del usuario
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); 
        }
    }

    /**
     * Endpoint para eliminar un elemento del catálogo personal (RF05).
     * Escucha en: DELETE /api/catalogo/elementos/1
     *
     * @param elementoId El ID del elemento a eliminar (de la URL).
     * @param principal El usuario (del token JWT).
     * @return 204 No Content (Éxito sin respuesta).
     */
    @DeleteMapping("/elementos/{elementoId}")
    public ResponseEntity<?> removeElementoDelCatalogo(
            @PathVariable Long elementoId,
            Principal principal) {
        
        try {
            catalogoService.removeElementoDelCatalogo(principal.getName(), elementoId);
            // 204 No Content es la respuesta estándar para un DELETE exitoso
            return ResponseEntity.noContent().build(); 
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404
        }
    }
}