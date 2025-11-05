package com.libratrack.api.controller;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.service.TipoService;
import jakarta.validation.Valid; // Import para @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ¡Import para la seguridad!
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión administrativa de Tipos de contenido.
 * Implementa los endpoints para crear y listar Tipos.
 *
 * Seguridad (RF03, RF14):
 * Toda esta clase está protegida. Solo los usuarios con la autoridad
 * 'ROLE_MODERADOR' pueden acceder a estas rutas.
 */
@RestController
@RequestMapping("/api/admin/tipos") // Ruta base para la administración de Tipos
@PreAuthorize("hasAuthority('ROLE_MODERADOR')") // ¡Seguridad a nivel de CLASE!
public class TipoController {

    @Autowired
    private TipoService tipoService;

    /**
     * Endpoint para obtener todos los Tipos existentes.
     * Escucha en: GET /api/admin/tipos
     *
     * (Seguridad: Heredada de la clase - solo Moderadores)
     *
     * @return ResponseEntity con una Lista de todas las entidades Tipo (200 OK).
     */
    @GetMapping
    public ResponseEntity<List<Tipo>> getAllTipos() {
        return ResponseEntity.ok(tipoService.getAllTipos());
    }

    /**
     * Endpoint para crear un nuevo Tipo (ej. "Anime", "Manga").
     * Escucha en: POST /api/admin/tipos
     *
     * (Seguridad: Heredada de la clase - solo Moderadores)
     *
     * @param tipo El objeto Tipo (del JSON) con el nombre a crear.
     * @return ResponseEntity:
     * - 201 (Created) con el nuevo Tipo creado.
     * - 400 (Bad Request) si el nombre está vacío o ya existe.
     */
    @PostMapping
    public ResponseEntity<?> createTipo(@Valid @RequestBody Tipo tipo) {
        // (@Valid activa las validaciones @NotBlank de la entidad Tipo)
        try {
            Tipo nuevoTipo = tipoService.createTipo(tipo);
            return new ResponseEntity<>(nuevoTipo, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            // Captura errores (ej. 'unique constraint' si el nombre ya existe)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400
        }
    }
}