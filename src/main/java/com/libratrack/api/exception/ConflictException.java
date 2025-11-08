// Archivo: src/main/java/com/libratrack/api/exception/ConflictException.java
package com.libratrack.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para manejar conflictos en la lógica de negocio (HTTP 409 Conflict).
 * Típicamente usado para duplicados (ej. email/username ya registrados,
 * elemento ya añadido al catálogo, reseña duplicada).
 */
@ResponseStatus(HttpStatus.CONFLICT) // <--- ¡Anotación clave!
public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor que acepta un mensaje detallado.
     */
    public ConflictException(String mensaje) {
        super(mensaje);
    }
}