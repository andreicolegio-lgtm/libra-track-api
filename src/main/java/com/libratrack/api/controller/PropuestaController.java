// Archivo: src/main/java/com/libratrack/api/controller/PropuestaController.java
package com.libratrack.api.controller;

import com.libratrack.api.dto.PropuestaRequestDTO;
import com.libratrack.api.dto.PropuestaResponseDTO;
import com.libratrack.api.service.PropuestaElementoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controlador REST para que los usuarios autenticados gestionen Propuestas (RF13).
 * REFACTORIZADO: Eliminado try-catch manual.
 */
@RestController
@RequestMapping("/api/propuestas")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class PropuestaController {

    @Autowired
    private PropuestaElementoService propuestaService;

    /**
     * Endpoint para que un usuario cree una nueva propuesta (RF13).
     *
     * REFACTORIZADO: Eliminado try-catch. Las excepciones se gestionan globalmente.
     */
    @PostMapping
    public ResponseEntity<PropuestaResponseDTO> createPropuesta(@Valid @RequestBody PropuestaRequestDTO dto, Principal principal) {
        
        String username = principal.getName();
        
        // El servicio lanza ResourceNotFoundException (404) si el usuario no existe.
        PropuestaResponseDTO nuevaPropuesta = propuestaService.createPropuesta(dto, username);
        
        return new ResponseEntity<>(nuevaPropuesta, HttpStatus.CREATED); // 201 Created
    }
}