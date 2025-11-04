package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.service.ElementoService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/elementos") // Todas las rutas aquí empiezan con /api/elementos
public class ElementoController {

    @Autowired
    private ElementoService elementoService;

    /**
     * Endpoint para obtener todos los elementos (RF09: Búsqueda Global).
     * URL: GET /api/elementos
     *
     * @return Una lista de todos los elementos.
     */
    @GetMapping
    public ResponseEntity<List<Elemento>> getAllElementos() {
        List<Elemento> elementos = elementoService.findAllElementos();
        return ResponseEntity.ok(elementos); // Devuelve 200 OK
    }

    /**
     * Endpoint para obtener un elemento por su ID (RF10: Ficha Detallada).
     * URL: GET /api/elementos/1 (donde 1 es el ID)
     *
     * @param id El ID del elemento a buscar, extraído de la URL.
     * @return El Elemento si se encuentra (200 OK) o un error (404 Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getElementoById(@PathVariable Long id) {
        Optional<Elemento> elementoOptional = elementoService.findElementoById(id);

        if (elementoOptional.isPresent()) {
            // Si encontramos el elemento, lo devolvemos
            return ResponseEntity.ok(elementoOptional.get()); // 200 OK
        } else {
            // Si no, devolvemos un error 404
            return new ResponseEntity<>("Elemento no encontrado", HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    /**
     * Endpoint para crear un nuevo elemento (RF13: Proponer).
     * URL: POST /api/elementos
     *
     * @param elementoDTO El DTO con los datos del elemento a crear.
     * @return El Elemento creado (201) o un error (400).
     */
    @PostMapping
    public ResponseEntity<?> createElemento(@Valid @RequestBody ElementoDTO elementoDTO) {
        // NOTA: Más adelante, el 'creadorId' lo sacaremos de un token JWT
        // en lugar de confiar en el DTO, pero para probar está bien.
        try {
            Elemento nuevoElemento = elementoService.createElemento(elementoDTO);
            return new ResponseEntity<>(nuevoElemento, HttpStatus.CREATED); // 201 Created
        } catch (Exception e) {
            // Captura cualquier error del servicio (ej. "Tipo no encontrado")
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }
}