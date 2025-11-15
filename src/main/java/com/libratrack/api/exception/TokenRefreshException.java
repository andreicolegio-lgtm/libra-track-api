// Archivo: src/main/java/com/libratrack/api/exception/TokenRefreshException.java
// (¡NUEVO ARCHIVO!)

package com.libratrack.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar errores de Tokens de Refresco (HTTP 403 Forbidden).
 * * Se usa HttpStatus.FORBIDDEN (403) en lugar de 401 porque el error 401
 * (Unauthorized) está reservado para el Access Token (JWT) caducado.
 * * Un 403 en el endpoint /refresh le dice al cliente: 
 * "Tu token de refresco es inválido o ha caducado. No lo intentes de nuevo. 
 * Debes volver a iniciar sesión."
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor que acepta el token y un mensaje.
     * @param token El token que causó el error.
     * @param mensaje El mensaje de error.
     */
    public TokenRefreshException(String token, String mensaje) {
        super(String.format("Error con el token [%s]: %s", token, mensaje));
    }
}