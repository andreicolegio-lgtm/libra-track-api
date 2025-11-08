// Archivo: src/main/java/com/libratrack/api/controller/ModeracionController.java
package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ResourceNotFoundException; // NUEVA IMPORTACIÓN
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.service.PropuestaElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controlador REST para las rutas de Moderación (RF14 y RF15).
 * REFACTORIZADO: Eliminado try-catch manual.
 */
@RestController
@RequestMapping("/api/moderacion")
public class ModeracionController {

    @Autowired
    private PropuestaElementoService propuestaService;

    @Autowired
    private UsuarioRepository usuarioRepo;

    // ... (getPropuestasPendientes sin cambios)

    /**
     * Endpoint para aprobar una propuesta (RF15).
     *
     * REFACTORIZADO: Eliminado try-catch.
     */
    @PostMapping("/aprobar/{propuestaId}")
    @PreAuthorize("hasAuthority('ROLE_MODERADOR')")
    public ResponseEntity<ElementoResponseDTO> aprobarPropuesta(@PathVariable Long propuestaId, Principal principal) {

        // 1. Obtenemos el nombre del moderador desde el token
        String revisorUsername = principal.getName();
        
        // 2. Buscamos la entidad Usuario del moderador (Lanza 404 si el token es inválido)
        Usuario revisor = usuarioRepo.findByUsername(revisorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Token de revisor inválido. Usuario no encontrado.")); // <-- 404

        // 3. ¡Usamos el ID real del moderador!
        Long revisorId = revisor.getId(); 

        // 4. Llama al servicio, que lanzará 404/409 si algo falla.
        ElementoResponseDTO nuevoElemento = propuestaService.aprobarPropuesta(propuestaId, revisorId);
        
        // 5. Devolvemos el nuevo elemento creado
        return ResponseEntity.ok(nuevoElemento);
    }
}