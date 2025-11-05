package com.libratrack.api.controller;

import com.libratrack.api.dto.ResenaDTO; // DTO para recibir datos
import com.libratrack.api.dto.ResenaResponseDTO; // DTO para enviar respuestas
import com.libratrack.api.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import para seguridad
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Import para obtener el usuario del token
import java.util.List;

/**
 * Controlador REST para la gestión de Reseñas (RF12).
 *
 * Seguridad:
 * Todas las rutas aquí requieren que el usuario esté autenticado
 * (tenga la autoridad 'ROLE_USER'), lo cual se define con @PreAuthorize.
 */
@RestController
@RequestMapping("/api/resenas") // Ruta base
@PreAuthorize("hasAuthority('ROLE_USER')") // Requiere ser 'ROLE_USER' para TODAS las rutas de este controlador
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    /**
     * Endpoint para obtener todas las reseñas de un elemento específico (RF12).
     * Escucha en: GET /api/resenas/elemento/1
     * (Donde 1 es el elementoId)
     *
     * @param elementoId El ID del elemento del cual queremos las reseñas.
     * @return ResponseEntity con una Lista de DTOs de las reseñas (200 OK).
     */
    @GetMapping("/elemento/{elementoId}")
    public ResponseEntity<List<ResenaResponseDTO>> getResenasDelElemento(@PathVariable Long elementoId) {
        // Llama al servicio, que ya devuelve una lista de DTOs (código limpio)
        List<ResenaResponseDTO> resenas = resenaService.getResenasByElementoId(elementoId);
        return ResponseEntity.ok(resenas); // Devuelve 200 OK
    }

    /**
     * Endpoint para crear una nueva reseña (RF12).
     * Escucha en: POST /api/resenas
     *
     * Seguridad (Refactor):
     * El 'usuarioId' no se acepta del DTO. Se extrae del token JWT
     * usando el objeto 'Principal' para garantizar que un usuario
     * solo puede publicar reseñas como él mismo.
     *
     * @param resenaDTO El DTO con los datos (elementoId, valoracion, textoResena).
     * @param principal El objeto de seguridad que contiene el 'username' del token.
     * @return ResponseEntity:
     * - 201 (Created) con el DTO de la reseña creada.
     * - 409 (Conflict) si el usuario ya ha reseñado ese elemento.
     */
    @PostMapping
    public ResponseEntity<?> createResena(@Valid @RequestBody ResenaDTO resenaDTO, Principal principal) {
        
        try {
            // Obtenemos el username del token (la fuente de verdad)
            String username = principal.getName();
            
            // Llamamos al servicio (refactorizado)
            ResenaResponseDTO nuevaResena = resenaService.createResena(resenaDTO, username);
            
            return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED); // 201 Created
        
        } catch (Exception e) {
            // Captura cualquier error del servicio (ej. "Ya has reseñado este elemento")
            // o si el 'elementoId' del DTO no existe.
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflicto
        }
    }
    
    // (En una V2, podríamos añadir endpoints PUT y DELETE para que un
    // usuario edite o borre su propia reseña).
}