// Archivo: src/main/java/com/libratrack/api/dto/LoginResponseDTO.java
// (¡ACTUALIZADO!)

package com.libratrack.api.dto;

/**
 * DTO (Data Transfer Object) para la RESPUESTA del Login (RF02).
 * --- ¡ACTUALIZADO (Sprint 10 / Refresh Tokens)! ---
 *
 * Esta clase es el "molde" del JSON que la API devuelve al
 * cliente (la app Flutter) cuando la autenticación es exitosa.
 * Ahora contiene ambos tokens: el Access Token (JWT corto) y
 * el Refresh Token (UUID largo).
 */
public class LoginResponseDTO {

    /**
     * El Access Token (JWT) de corta duración (ej. 30 min).
     * Se usa para autenticar las peticiones normales a la API.
     */
    private String accessToken; // <-- Renombrado (antes 'token')

    /**
     * El Refresh Token (UUID) de larga duración (ej. 7 días).
     * Se usa para solicitar silenciosamente un nuevo Access Token
     * cuando el primero caduca.
     */
    private String refreshToken; // <-- ¡NUEVO CAMPO!

    /**
     * El tipo de token. Es una convención estándar de la industria
     * (OAuth 2.0) incluir esto.
     * (Ej: "Authorization: Bearer [accessToken]")
     */
    private String tipo = "Bearer";

    // --- Constructor ---

    /**
     * Constructor que toma ambos tokens generados por los servicios.
     * @param accessToken El string del Access Token (JWT).
     * @param refreshToken El string del Refresh Token (UUID).
     */
    public LoginResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // --- Getters ---
    // Necesarios para que Spring/Jackson pueda leer los valores
    // y construir el JSON de respuesta.

    public String getAccessToken() { // <-- Renombrado
        return accessToken;
    }

    public String getRefreshToken() { // <-- ¡NUEVO GETTER!
        return refreshToken;
    }

    public String getTipo() {
        return tipo;
    }
}