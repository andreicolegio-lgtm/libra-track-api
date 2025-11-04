package com.libratrack.api.controller;

import com.libratrack.api.dto.ResenaDTO;
import com.libratrack.api.dto.ResenaResponseDTO;
import com.libratrack.api.entity.Resena;
import com.libratrack.api.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas") // Todas las rutas aquí empiezan con /api/resenas
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    /**
     * Endpoint para obtener todas las reseñas de un elemento específico (RF12).
     * URL: GET /api/resenas/elemento/1
     */
    @GetMapping("/elemento/{elementoId}")
    public ResponseEntity<List<ResenaResponseDTO>> getResenasDelElemento(@PathVariable Long elementoId) {
        List<ResenaResponseDTO> resenasDTOs = resenaService.getResenasByElementoId(elementoId);
        return ResponseEntity.ok(resenasDTOs); // Devuelve 200 OK con la lista de DTOs
    }

    /**
     * Endpoint para crear una nueva reseña (RF12).
     * URL: POST /api/resenas
     * Cuerpo (JSON): { "usuarioId": 1, "elementoId": 1, "valoracion": 5, "textoResena": "..." }
     */
    @PostMapping
    public ResponseEntity<?> createResena(@Valid @RequestBody ResenaDTO resenaDTO) {
        // NOTA: En una app real, el 'usuarioId' lo sacaríamos del token JWT
        // para asegurar que un usuario solo puede publicar como él mismo.
        try {
            Resena nuevaResena = resenaService.createResena(resenaDTO);
            return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            // Captura cualquier error del servicio (ej. "Ya has reseñado este elemento")
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflicto
        }
    }
}