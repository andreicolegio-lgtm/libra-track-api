// Archivo: src/main/java/com/libratrack/api/controller/ResenaController.java
package com.libratrack.api.controller;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
import com.libratrack.api.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controlador REST para la gestión de Reseñas (RF12).
 * REFACTORIZADO: Eliminado try-catch manual.
 */
@RestController
@RequestMapping("/api/resenas")
@PreAuthorize("hasAuthority('ROLE_USER')") 
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    // ... (getResenasDelElemento sin cambios)

    /**
     * Endpoint para crear una nueva reseña (RF12).
     *
     * REFACTORIZADO: Eliminado try-catch. La validación (@Valid) y las
     * excepciones de negocio (404/409) se gestionan globalmente.
     */
    @PostMapping
    public ResponseEntity<ResenaResponseDTO> createResena(@Valid @RequestBody ResenaDTO resenaDTO, Principal principal) {
        
        // 1. Obtenemos el username del token (la fuente de verdad)
        String username = principal.getName();
        
        // 2. Llama al servicio (si falla, el GlobalExceptionHandler se encarga)
        ResenaResponseDTO nuevaResena = resenaService.createResena(resenaDTO, username);
        
        return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED); // 201 Created
    }
}