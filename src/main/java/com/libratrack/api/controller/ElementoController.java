// Archivo: src/main/java/com/libratrack/api/controller/ElementoController.java
// (¡MODIFICADO POR GEMINI!)

package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO; 
// --- ¡NUEVA IMPORTACIÓN! ---
import com.libratrack.api.dto.ElementoRelacionDTO; 
import com.libratrack.api.service.ElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// --- ¡NUEVA IMPORTACIÓN! ---
import java.util.List; 
import java.util.Optional;

@RestController
@RequestMapping("/api/elementos") 
public class ElementoController {

    @Autowired 
    private ElementoService elementoService;

    /**
     * Endpoint para obtener todos los elementos o para buscar por título (RF09).
     * (Sin cambios)
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<ElementoResponseDTO>> getAllElementos(
            @RequestParam(value = "search", required = false) String searchText,
            @RequestParam(value = "tipo", required = false) String tipoName,
            @RequestParam(value = "genero", required = false) String generoName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
        ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo").ascending());
        Page<ElementoResponseDTO> pagina = elementoService.findAllElementos(pageable, searchText, tipoName, generoName);
        return ResponseEntity.ok(pagina);
    }

    /**
     * Endpoint para obtener un elemento por su ID (RF10: Ficha Detallada).
     * (Sin cambios)
     */
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ElementoResponseDTO> getElementoById(@PathVariable Long id) {
        
        // (Llamada al servicio ya refactorizado que carga todo)
        Optional<ElementoResponseDTO> elementoDTOOptional = elementoService.findElementoById(id);

        if (elementoDTOOptional.isPresent()) {
            return ResponseEntity.ok(elementoDTOOptional.get()); // 200 OK
        } else {
            // El servicio ya no devuelve Optional, lanza una excepción que es
            // manejada por el GlobalExceptionHandler (HTTP 404).
            // Pero mantenemos este bloque por si acaso.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
    }

    // --- ¡NUEVO ENDPOINT! (Añadido por Gemini) ---
    /**
     * Endpoint para obtener una lista simple (id, titulo, imagen) de TODOS
     * los elementos.
     * Usado por el formulario de Admin para rellenar el selector de
     * precuelas/secuelas.
     * Solo accesible por Moderadores y Admins.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_MODERADOR', 'ROLE_ADMIN')")
    @GetMapping("/all-simple")
    public ResponseEntity<List<ElementoRelacionDTO>> getAllElementosSimple() {
        List<ElementoRelacionDTO> lista = elementoService.findAllSimple();
        return ResponseEntity.ok(lista);
    }
    // --- FIN DE ENDPOINT AÑADIDO ---
}