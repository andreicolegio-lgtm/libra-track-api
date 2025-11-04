package com.libratrack.api.dto;

/**
 * DTO para la petición de crear una nueva PropuestaElemento (RF13).
 * El usuario enviará un JSON con estos datos.
 */
public class PropuestaRequestDTO {

    // No necesitamos el ID del proponente aquí, porque
    // lo sacaremos del Token JWT (sabremos quién hace la petición).

    private String tituloSugerido;
    private String descripcionSugerida;
    private String tipoSugerido; // Ej: "Serie"
    private String generosSugeridos; // Ej: "Ciencia Ficción, Drama"

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