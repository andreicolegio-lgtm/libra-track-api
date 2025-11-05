package com.libratrack.api.dto;

/**
 * DTO (Data Transfer Object) para la RESPUESTA del Login (RF02).
 *
 * Esta clase es el "molde" del JSON que la API devuelve al
 * cliente (la app Flutter) cuando la autenticación es exitosa.
 * Contiene el token JWT que la app debe guardar de forma segura
 * para usarlo en futuras peticiones.
 */
public class LoginResponseDTO {

    /**
     * El JSON Web Token (JWT) en formato string.
     * Es un string largo y cifrado que contiene la identidad del usuario.
     */
    private String token;

    /**
     * El tipo de token. Es una convención estándar de la industria
     * (OAuth 2.0) incluir esto. Ayuda al cliente a saber
     * cómo debe formatear la cabecera 'Authorization'.
     * (Ej: "Authorization: Bearer [token]")
     */
    private String tipo = "Bearer";

    // --- Constructor ---

    /**
     * Constructor que toma el token generado por el JwtService.
     * @param token El string del token JWT.
     */
    public LoginResponseDTO(String token) {
        this.token = token;
    }

    // --- Getters ---
    // Necesarios para que Spring/Jackson pueda leer los valores
    // y construir el JSON de respuesta.

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }
}