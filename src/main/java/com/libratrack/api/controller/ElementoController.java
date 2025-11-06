package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO; // DTO para enviar respuestas (¡Evita error 500!)
import com.libratrack.api.service.ElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import para seguridad a nivel de método
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para las rutas del catálogo principal (Elementos).
 * Todas las rutas aquí están protegidas y requieren autenticación (un token JWT).
 * Implementa los endpoints para RF09 (Búsqueda) y RF10 (Ficha detallada).
 */
@RestController // Indica a Spring que esta clase es un Controlador y devuelve JSON
@RequestMapping("/api/elementos") // Todas las rutas aquí empiezan con /api/elementos
public class ElementoController {

    @Autowired // Inyecta el servicio que contiene la lógica de negocio
    private ElementoService elementoService;

    /**
     * Endpoint para obtener todos los elementos o para buscar por título (RF09).
     * Escucha en: GET /api/elementos?search=texto
     *
     * @param searchText El parámetro de búsqueda (opcional) de la URL (@RequestParam).
     * @return ResponseEntity con una Lista de DTOs de los elementos (200 OK).
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<ElementoResponseDTO>> getAllElementos(
            @RequestParam(value = "search", required = false) String searchText,
            @RequestParam(value = "tipo", required = false) String tipoName,
            @RequestParam(value = "genero", required = false) String generoName) {

        // Llama al servicio, pasándole los 3 parámetros de filtro
        List<ElementoResponseDTO> elementos = elementoService.findAllElementos(searchText, tipoName, generoName);
        return ResponseEntity.ok(elementos); // Devuelve 200 OK
    }

    /**
     * Endpoint para obtener un elemento por su ID (RF10: Ficha Detallada).
     * Escucha en: GET /api/elementos/1 (donde 1 es el ID)
     *
     * Seguridad:
     * Protegido con @PreAuthorize, requiere ser 'ROLE_USER'.
     *
     * @param id El ID del elemento a buscar (extraído de la URL con @PathVariable).
     * @return ResponseEntity:
     * - 200 (OK) con el DTO del Elemento si se encuentra.
     * - 404 (Not Found) si el ID no existe en la base de datos.
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getElementoById(@PathVariable Long id) {
        
        // Llama al servicio, que devuelve un Optional<ElementoResponseDTO>
        Optional<ElementoResponseDTO> elementoDTOOptional = elementoService.findElementoById(id);

        if (elementoDTOOptional.isPresent()) {
            // Si encontramos el elemento, lo devolvemos
            return ResponseEntity.ok(elementoDTOOptional.get()); // 200 OK
        } else {
            // Si no, devolvemos un error 404 claro
            return new ResponseEntity<>("Elemento no encontrado con id: " + id, HttpStatus.NOT_FOUND); // 404
        }
    }

    /*
     * NOTA SOBRE EL POST /api/elementos (RF13):
     *
     * La lógica para *crear* elementos (RF13) la hemos implementado
     * en el 'PropuestaController' (para usuarios) y en el
     * 'ModeracionController' (para moderadores), ya que tu diseño
     * requiere que toda la creación de contenido pase por la cola de moderación.
     *
     * (Si quisiéramos un endpoint de Admin para crear un elemento 'OFICIAL'
     * directamente, lo añadiríamos aquí con @PreAuthorize("hasAuthority('ROLE_ADMIN')")).
     */
}