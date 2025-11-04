package com.libratrack.api.controller;

import com.libratrack.api.dto.CatalogoUpdateDTO;
import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.service.CatalogoPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// ¡IMPORTANTE! Todas las rutas aquí empiezan con /api/catalogo/{usuarioId}
// {usuarioId} es una variable que capturaremos de la URL.
@RequestMapping("/api/catalogo/{usuarioId}")
public class CatalogoPersonalController {

    @Autowired
    private CatalogoPersonalService catalogoService;

    /**
     * Endpoint para obtener el catálogo completo de un usuario (RF08).
     * URL: GET /api/catalogo/1
     * (Donde 1 es el usuarioId)
     */
    @GetMapping
    public ResponseEntity<List<CatalogoPersonal>> getCatalogoDelUsuario(@PathVariable Long usuarioId) {
        // NOTA: En una app real, obtendríamos el usuarioId del token,
        // no de la URL, para que un usuario no pueda ver el catálogo de otro.
        List<CatalogoPersonal> catalogo = catalogoService.getCatalogoByUsuarioId(usuarioId);
        return ResponseEntity.ok(catalogo);
    }

    /**
     * Endpoint para añadir un elemento al catálogo de un usuario (RF05).
     * URL: POST /api/catalogo/1/elementos/1
     * (Donde el primer '1' es usuarioId y el segundo '1' es elementoId)
     */
    @PostMapping("/elementos/{elementoId}")
    public ResponseEntity<?> addElementoAlCatalogo(
            @PathVariable Long usuarioId,
            @PathVariable Long elementoId) {
        try {
            CatalogoPersonal nuevaEntrada = catalogoService.addElementoAlCatalogo(usuarioId, elementoId);
            return new ResponseEntity<>(nuevaEntrada, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflicto (ej. ya existe)
        }
    }

    /**
     * Endpoint para actualizar el estado/progreso de un elemento (RF06, RF07).
     * URL: PUT /api/catalogo/1/elementos/1
     * Cuerpo (JSON): { "estadoPersonal": "EN_PROGRESO", "progresoEspecifico": "T4:E3" }
     */
    @PutMapping("/elementos/{elementoId}")
    public ResponseEntity<?> updateElementoDelCatalogo(
            @PathVariable Long usuarioId,
            @PathVariable Long elementoId,
            @RequestBody CatalogoUpdateDTO dto) { // Usa el DTO que creamos
        try {
            CatalogoPersonal entradaActualizada = catalogoService.updateEntradaCatalogo(usuarioId, elementoId, dto);
            return ResponseEntity.ok(entradaActualizada); // 200 OK
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 No Encontrado
        }
    }

    /**
     * Endpoint para eliminar un elemento del catálogo de un usuario.
     * URL: DELETE /api/catalogo/1/elementos/1
     */
    @DeleteMapping("/elementos/{elementoId}")
    public ResponseEntity<?> removeElementoDelCatalogo(
            @PathVariable Long usuarioId,
            @PathVariable Long elementoId) {
        try {
            catalogoService.removeElementoDelCatalogo(usuarioId, elementoId);
            return ResponseEntity.noContent().build(); // 204 No Content (Éxito, sin respuesta)
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 No Encontrado
        }
    }
}