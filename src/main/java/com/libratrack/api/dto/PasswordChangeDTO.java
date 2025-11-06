package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para el flujo de cambio de contraseña (RF04).
 * Transporta la contraseña actual (para verificación) y la nueva contraseña.
 */
public class PasswordChangeDTO {

    @NotBlank(message = "La contraseña actual no puede estar vacía")
    private String contraseñaActual;

    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String nuevaContraseña;

    // --- Getters y Setters ---

    public String getContraseñaActual() {
        return contraseñaActual;
    }

    public void setContraseñaActual(String contraseñaActual) {
        this.contraseñaActual = contraseñaActual;
    }

    public String getNuevaContraseña() {
        return nuevaContraseña;
    }

    public void setNuevaContraseña(String nuevaContraseña) {
        this.nuevaContraseña = nuevaContraseña;
    }
}