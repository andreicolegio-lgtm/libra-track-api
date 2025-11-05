package com.libratrack.api.controller;

import com.libratrack.api.dto.PropuestaRequestDTO; // DTO para RECIBIR datos
import com.libratrack.api.dto.PropuestaResponseDTO; // DTO para ENVIAR respuestas (evita error 500)
import com.libratrack.api.service.PropuestaElementoService;
import jakarta.validation.Valid; // Para activar la validación en el DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Para la seguridad a nivel de método
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // ¡Importante! Para obtener el usuario del token

/**
 * Controlador REST para que los usuarios autenticados gestionen Propuestas.
 * Esta ruta está protegida y solo es accesible para usuarios logueados.
 * Implementa el endpoint para RF13: Proponer nuevo contenido.
 */
@RestController
@RequestMapping("/api/propuestas")
@PreAuthorize("hasAuthority('ROLE_USER')") // Seguridad: Solo usuarios con 'ROLE_USER' (logueados) pueden proponer
public class PropuestaController {

    @Autowired
    private PropuestaElementoService propuestaService; // Inyecta el "cerebro"

    /**
     * Endpoint para que un usuario cree una nueva propuesta (RF13).
     * Escucha en: POST /api/propuestas
     *
     * Seguridad (Mejor Práctica):
     * El ID/nombre del proponente se extrae del token JWT (usando 'Principal'),
     * no del DTO. Esto garantiza que un usuario solo pueda proponer
     * contenido en su propio nombre.
     *
     * @param dto El DTO (PropuestaRequestDTO) con los datos sugeridos (título, tipo, etc.)
     * @param principal Objeto inyectado por Spring Security que contiene el 'username'
     * del usuario autenticado (extraído del token).
     * @return ResponseEntity:
     * - 201 (Created) con el DTO de la propuesta creada (PropuestaResponseDTO).
     * - 400 (Bad Request) si la validación falla o el usuario no se encuentra.
     */
    @PostMapping
    public ResponseEntity<?> createPropuesta(@Valid @RequestBody PropuestaRequestDTO dto, Principal principal) {
        
        // 1. Obtener el 'username' del token JWT (la fuente de verdad)
        // principal.getName() devuelve el "subject" de nuestro token JWT.
        String username = principal.getName();
        
        try {
            // 2. Llamar al servicio para crear la propuesta en la "sala de espera"
            PropuestaResponseDTO nuevaPropuesta = propuestaService.createPropuesta(dto, username);
            
            // 3. Devolver la respuesta exitosa
            return new ResponseEntity<>(nuevaPropuesta, HttpStatus.CREATED); // 201 Created

        } catch (Exception e) {
            // 4. Capturar cualquier error del servicio (ej. "Usuario no encontrado")
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400
        }
    }
}