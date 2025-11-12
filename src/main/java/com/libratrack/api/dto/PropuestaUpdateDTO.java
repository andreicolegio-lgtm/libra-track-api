package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

public class PropuestaUpdateDTO {

    // ... (Campos existentes: titulo, desc, tipo, generos ... sin cambios) ...
    @NotBlank
    @Size(max = 255)
    private String tituloSugerido;
    @NotBlank
    @Size(max = 5000)
    private String descripcionSugerida;
    @NotBlank
    private String tipoSugerido; 
    @NotBlank
    private String generosSugeridos; 

    // --- ¡NUEVO CAMPO AÑADIDO! (Petición 6) ---
    @Size(max = 255) // Para la URL de GCS
    private String urlImagen;
    
    // --- Campos de Progreso (Refactorizados) ---
    @Size(max = 255)
    private String episodiosPorTemporada; 
    @Min(value = 1)
    private Integer totalUnidades; 
    @Min(value = 1)
    private Integer totalCapitulosLibro; 
    @Min(value = 1)
    private Integer totalPaginasLibro; 

    // --- Getters y Setters ---
    
    // ... (Getters/Setters existentes ... sin cambios) ...
    public String getTituloSugerido() { return tituloSugerido; }
    public void setTituloSugerido(String tituloSugerido) { this.tituloSugerido = tituloSugerido; }
    public String getDescripcionSugerida() { return descripcionSugerida; }
    public void setDescripcionSugerida(String descripcionSugerida) { this.descripcionSugerida = descripcionSugerida; }
    public String getTipoSugerido() { return tipoSugerido; }
    public void setTipoSugerido(String tipoSugerido) { this.tipoSugerido = tipoSugerido; }
    public String getGenerosSugeridos() { return generosSugeridos; }
    public void setGenerosSugeridos(String generosSugeridos) { this.generosSugeridos = generosSugeridos; }
    
    // --- ¡NUEVO GETTER/SETTER AÑADIDO! ---
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    // ... (Getters/Setters de Progreso ... sin cambios) ...
    public String getEpisodiosPorTemporada() { return episodiosPorTemporada; }
    public void setEpisodiosPorTemporada(String episodiosPorTemporada) { this.episodiosPorTemporada = episodiosPorTemporada; }
    public Integer getTotalUnidades() { return totalUnidades; }
    public void setTotalUnidades(Integer totalUnidades) { this.totalUnidades = totalUnidades; }
    public Integer getTotalCapitulosLibro() { return totalCapitulosLibro; }
    public void setTotalCapitulosLibro(Integer totalCapitulosLibro) { this.totalCapitulosLibro = totalCapitulosLibro; }
    public Integer getTotalPaginasLibro() { return totalPaginasLibro; }
    public void setTotalPaginasLibro(Integer totalPaginasLibro) { this.totalPaginasLibro = totalPaginasLibro; }
}