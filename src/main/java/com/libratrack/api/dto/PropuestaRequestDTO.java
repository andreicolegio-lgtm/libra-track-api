package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO para RECIBIR una propuesta de Elemento del cliente (RF13).
 */
public class PropuestaRequestDTO {

    // --- Campos Requeridos del Elemento ---

    @NotBlank(message = "El título sugerido no puede estar vacío")
    @Size(max = 255, message = "El título sugerido no puede exceder los 255 caracteres")
    private String tituloSugerido;

    @NotBlank(message = "La descripción sugerida no puede estar vacía")
    @Size(max = 5000, message = "La descripción sugerida no puede exceder los 5000 caracteres")
    private String descripcionSugerida;
    
    // --- Campos de Relación ---
    
    @NotNull(message = "El ID del Tipo no puede ser nulo")
    private Long tipoIdSugerido;

    @NotNull(message = "La lista de IDs de Géneros no puede ser nula")
    private List<Long> generoIdsSugeridos;

    // --- Campo de Multimedia (NUEVO) ---
    /**
     * URL de la imagen de portada sugerida para el Elemento. (RF13)
     */
    private String imagenPortadaUrl; // <--- ¡AÑADIDO AQUÍ!

    
    // --- Getters y Setters ---

    public String getTituloSugerido() {
        return tituloSugerido;
    }

    public void setTituloSugerido(String tituloSugerido) {
        this.tituloSugerido = tituloSugerido;
    }

    public String getDescripcionSugerida() {
        return descripcionSugerida;
    }

    public void setDescripcionSugerida(String descripcionSugerida) {
        this.descripcionSugerida = descripcionSugerida;
    }

    public Long getTipoIdSugerido() {
        return tipoIdSugerido;
    }

    public void setTipoIdSugerido(Long tipoIdSugerido) {
        this.tipoIdSugerido = tipoIdSugerido;
    }

    public List<Long> getGeneroIdsSugeridos() {
        return generoIdsSugeridos;
    }

    public void setGeneroIdsSugeridos(List<Long> generoIdsSugeridos) {
        this.generoIdsSugeridos = generoIdsSugeridos;
    }

    // --- Getter y Setter para la URL de la imagen (NUEVO) ---
    public String getImagenPortadaUrl() {
        return imagenPortadaUrl;
    }

    public void setImagenPortadaUrl(String imagenPortadaUrl) {
        this.imagenPortadaUrl = imagenPortadaUrl;
    }
}