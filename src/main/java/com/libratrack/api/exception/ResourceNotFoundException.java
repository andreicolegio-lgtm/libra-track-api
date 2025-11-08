// Archivo: src/main/java/com/libratrack/api/exception/ResourceNotFoundException.java
package com.libratrack.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para manejar recursos no encontrados (HTTP 404 Not Found).
 * Spring la detectará y automáticamente devolverá el código 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // <--- ¡Anotación clave!
public class ResourceNotFoundException extends RuntimeException {

    // Identificador de versión (buena práctica de Java)
    private static final long serialVersionUID = 1L;

    /**
     * Constructor que acepta un mensaje detallado.
     */
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}