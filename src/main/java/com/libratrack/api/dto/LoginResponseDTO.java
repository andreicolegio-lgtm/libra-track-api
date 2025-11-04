package com.libratrack.api.dto;

/**
 * DTO para la respuesta del Login.
 * Contiene el token JWT que el cliente (móvil) debe guardar.
 */
public class LoginResponseDTO {

    private String token;
    private String tipo = "Bearer"; // Estándar para tokens JWT

    // --- Constructor ---
    public LoginResponseDTO(String token) {
        this.token = token;
    }

    // --- Getters ---
    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }
}