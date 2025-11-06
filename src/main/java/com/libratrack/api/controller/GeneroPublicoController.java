package com.libratrack.api.controller;

import com.libratrack.api.entity.Genero;
import com.libratrack.api.service.GeneroService;
import com.libratrack.api.dto.GeneroResponseDTO; // ¡NUEVA IMPORTACIÓN!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors; // ¡NUEVA IMPORTACIÓN!

/**
 * Controlador REST para la consulta pública de Géneros de contenido (RF09).
 */
@RestController
@RequestMapping("/api/generos")
public class GeneroPublicoController {

    @Autowired
    private GeneroService generoService;

    /**
     * Endpoint para obtener todos los Géneros existentes.
     * Escucha en: GET /api/generos
     *
     * REFACTORIZADO: Ahora devuelve List<GeneroResponseDTO> en lugar de List<Genero>.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GeneroResponseDTO>> getAllGenerosPublico() {
        
        // 1. Obtener la lista de entidades
        List<Genero> generos = generoService.getAllGeneros();
        
        // 2. Mapear (convertir) cada entidad al DTO de respuesta
        List<GeneroResponseDTO> dtos = generos.stream()
            .map(GeneroResponseDTO::new)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(dtos);
    }
}