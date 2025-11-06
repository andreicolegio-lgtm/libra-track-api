package com.libratrack.api.controller;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.service.TipoService;
import com.libratrack.api.dto.TipoResponseDTO; // ¡NUEVA IMPORTACIÓN!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors; // ¡NUEVA IMPORTACIÓN!

/**
 * Controlador REST para la consulta pública de Tipos de contenido (RF09).
 */
@RestController
@RequestMapping("/api/tipos") 
public class TipoPublicoController {

    @Autowired
    private TipoService tipoService;

    /**
     * Endpoint para obtener todos los Tipos existentes.
     * Escucha en: GET /api/tipos
     *
     * REFACTORIZADO: Ahora devuelve List<TipoResponseDTO> en lugar de List<Tipo>.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<List<TipoResponseDTO>> getAllTiposPublico() {
        
        // 1. Obtener la lista de entidades
        List<Tipo> tipos = tipoService.getAllTipos();
        
        // 2. Mapear (convertir) cada entidad al DTO de respuesta
        List<TipoResponseDTO> dtos = tipos.stream()
            .map(TipoResponseDTO::new)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(dtos);
    }
}