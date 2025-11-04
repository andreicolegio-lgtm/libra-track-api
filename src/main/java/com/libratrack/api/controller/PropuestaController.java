package com.libratrack.api.controller;

import com.libratrack.api.dto.PropuestaRequestDTO;
import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.service.PropuestaElementoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Para obtener el usuario del token
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Para obtener el nombre del usuario

@RestController
@RequestMapping("/api/propuestas")
public class PropuestaController {

    @Autowired
    private PropuestaElementoService propuestaService;

    /**
     * Endpoint para que un usuario cree una nueva propuesta (RF13).
     * El ID del usuario se obtiene del token JWT, no del DTO.
     * URL: POST /api/propuestas
     */
    @PostMapping
    public ResponseEntity<?> createPropuesta(@Valid @RequestBody PropuestaRequestDTO dto, Principal principal) {
        // 'Principal' es un objeto de seguridad que contiene
        // el nombre del usuario autenticado (gracias al JWT)
        if (principal == null) {
            return new ResponseEntity<>("No autorizado", HttpStatus.UNAUTHORIZED);
        }
        String username = principal.getName();

        try {
            PropuestaElemento nuevaPropuesta = propuestaService.createPropuesta(dto, username);
            return new ResponseEntity<>(nuevaPropuesta, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400
        }
    }
}