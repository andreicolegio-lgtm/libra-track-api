// Archivo: src/main/java/com/libratrack/api/dto/GoogleTokenDTO.java
// (¡NUEVO ARCHIVO!)

package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para recibir el ID Token de Google enviado desde el cliente (Flutter).
 */
public class GoogleTokenDTO {

    @NotBlank(message = "El token de Google no puede estar vacío")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}