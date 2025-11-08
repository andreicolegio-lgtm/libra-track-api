// Archivo: src/main/java/com/libratrack/api/controller/CatalogoPersonalController.java
package com.libratrack.api.controller;

import com.libratrack.api.dto.CatalogoPersonalResponseDTO;
import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.service.CatalogoPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;

import java.security.Principal; 
import java.util.List;

/**
 * Controlador REST para el catálogo personal de un usuario.
 * REFACTORIZADO: Eliminados los try-catch manuales.
 */
@RestController
@RequestMapping("/api/catalogo") 
@PreAuthorize("hasAuthority('ROLE_USER')") 
public class CatalogoPersonalController {

    @Autowired
    private CatalogoPersonalService catalogoService;

    // ... (getMiCatalogo sin cambios, ya estaba limpio)

    /**
     * Endpoint para obtener el catálogo completo del usuario autenticado (RF08).
     */
    @GetMapping
    public ResponseEntity<List<CatalogoPersonalResponseDTO>> getMiCatalogo(Principal principal) {
        String username = principal.getName();
        
        List<CatalogoPersonalResponseDTO> catalogo = catalogoService.getCatalogoByUsername(username);
        return ResponseEntity.ok(catalogo);
    }

    /**
     * Endpoint para añadir un elemento al catálogo personal del usuario (RF05).
     *
     * REFACTORIZADO: Eliminado try-catch.
     */
    @PostMapping("/elementos/{elementoId}")
    public ResponseEntity<CatalogoPersonalResponseDTO> addElementoAlCatalogo(
            @PathVariable Long elementoId,
            Principal principal) {
        
        // Si falla, el servicio lanza 404 (Usuario/Elemento no existe) o 409 (Ya en catálogo).
        CatalogoPersonalResponseDTO nuevaEntrada = catalogoService.addElementoAlCatalogo(principal.getName(), elementoId);
        return new ResponseEntity<>(nuevaEntrada, HttpStatus.CREATED); // 201 Created
    }

    /**
     * Endpoint para actualizar el estado/progreso de un elemento (RF06, RF07).
     *
     * REFACTORIZADO: Eliminado try-catch.
     */
    @PutMapping("/elementos/{elementoId}")
    public ResponseEntity<CatalogoPersonalResponseDTO> updateElementoDelCatalogo(
            @PathVariable Long elementoId,
            @RequestBody CatalogoUpdateDTO dto,
            Principal principal) {
        
        // Si falla, el servicio lanza 404 (No está en el catálogo).
        CatalogoPersonalResponseDTO entradaActualizada = catalogoService.updateEntradaCatalogo(principal.getName(), elementoId, dto);
        return ResponseEntity.ok(entradaActualizada); // 200 OK
    }

    /**
     * Endpoint para eliminar un elemento del catálogo personal (RF05).
     *
     * REFACTORIZADO: Eliminado try-catch.
     */
    @DeleteMapping("/elementos/{elementoId}")
    public ResponseEntity<Void> removeElementoDelCatalogo(
            @PathVariable Long elementoId,
            Principal principal) {
        
        // Si falla, el servicio lanza 404 (No está en el catálogo).
        catalogoService.removeElementoDelCatalogo(principal.getName(), elementoId);
        // 204 No Content es la respuesta estándar para un DELETE exitoso
        return ResponseEntity.noContent().build(); 
    }
}