package com.libratrack.api.controller;

import com.libratrack.api.entity.Genero; // Importa la entidad Genero
import com.libratrack.api.service.GeneroService; // Importa el servicio Genero
import jakarta.validation.Valid; // Import para @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ¡Import para la seguridad!
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión administrativa de Géneros de contenido.
 * Implementa los endpoints para crear y listar Géneros.
 *
 * Seguridad (RF03, RF14):
 * Esta clase implementa la seguridad basada en roles. La anotación @PreAuthorize
 * a nivel de clase bloquea todos los endpoints, permitiendo el acceso
 * solo a usuarios que tengan la autoridad 'ROLE_MODERADOR'.
 */
@RestController
@RequestMapping("/api/admin/generos") // Ruta base para la administración de Géneros
@PreAuthorize("hasAuthority('ROLE_MODERADOR')") // ¡Seguridad a nivel de CLASE!
public class GeneroController {

    @Autowired
    private GeneroService generoService; // Inyecta el "cerebro"

    /**
     * Endpoint para obtener todos los Géneros existentes.
     * Escucha en: GET /api/admin/generos
     *
     * (Seguridad: Heredada de la clase - solo Moderadores)
     *
     * @return ResponseEntity con una Lista de todas las entidades Genero (200 OK).
     */
    @GetMapping
    public ResponseEntity<List<Genero>> getAllGeneros() {
        return ResponseEntity.ok(generoService.getAllGeneros());
    }

    /**
     * Endpoint para crear un nuevo Genero (ej. "Ciencia Ficción", "Drama").
     * Escucha en: POST /api/admin/generos
     *
     * (Seguridad: Heredada de la clase - solo Moderadores)
     *
     * @param genero El objeto Genero (del JSON) con el nombre a crear.
     * @return ResponseEntity:
     * - 201 (Created) con el nuevo Genero creado.
     * - 400 (Bad Request) si el nombre está vacío o ya existe.
     */
    @PostMapping
    public ResponseEntity<?> createGenero(@Valid @RequestBody Genero genero) {
        // (@Valid activa las validaciones @NotBlank de la entidad Genero)
        try {
            Genero nuevoGenero = generoService.createGenero(genero);
            return new ResponseEntity<>(nuevoGenero, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            // Captura errores (ej. 'unique constraint' si el nombre ya existe)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400
        }
    }
}