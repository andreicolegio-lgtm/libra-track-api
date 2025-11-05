package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para la PETICIÓN de crear una nueva PropuestaElemento.
 *
 * Este es el "molde" del JSON que la app Flutter enviará cuando
 * un usuario autenticado quiera proponer nuevo contenido (RF13).
 *
 * Seguridad (Mejor Práctica):
 * Este DTO *no* contiene un 'usuarioId' o 'proponenteId'.
 * Para prevenir que un usuario intente proponer contenido en nombre de otro,
 * el ID del proponente se obtendrá *exclusivamente* del token JWT
 * (el objeto 'Principal') en la capa del Controlador (PropuestaController).
 */
public class PropuestaRequestDTO {

    /**
     * El título que el usuario sugiere para el nuevo elemento (RF13).
     * @NotBlank asegura que el usuario no puede enviar un título vacío.
     */
    @NotBlank(message = "El título sugerido no puede estar vacío")
    @Size(max = 255)
    private String tituloSugerido;

    /**
     * La descripción o sinopsis sugerida (opcional).
     */
    private String descripcionSugerida;

    /**
     * El TIPO de contenido que el usuario sugiere (ej. "Anime", "Serie Web").
     * Esto se almacena como un String simple, y será "traducido"
     * a una entidad 'Tipo' por un Moderador durante la aprobación (RF15).
     */
    @Size(max = 100)
    private String tipoSugerido;

    /**
     * Los GÉNEROS que el usuario sugiere (ej. "Aventura, Fantasía, Shonen").
     * Se almacena como un String simple (separado por comas).
     * Será "traducido" a entidades 'Genero' por un Moderador (RF15).
     */
    @Size(max = 255)
    private String generosSugeridos;

    
    // --- Getters y Setters ---
    // Necesarios para que Spring/Jackson pueda mapear el JSON
    // entrante (en una petición POST) a este objeto Java.

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

    public String getTipoSugerido() {
        return tipoSugerido;
    }

    public void setTipoSugerido(String tipoSugerido) {
        this.tipoSugerido = tipoSugerido;
    }

    public String getGenerosSugeridos() {
        return generosSugeridos;
    }

    public void setGenerosSugeridos(String generosSugeridos) {
        this.generosSugeridos = generosSugeridos;
    }
}