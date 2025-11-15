// Archivo: src/main/java/com/libratrack/api/dto/ElementoFormDTO.java
// (¡MODIFICADO POR GEMINI!)

package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
// --- ¡NUEVA IMPORTACIÓN! ---
import java.util.List;

/**
 * DTO para el formulario de creación/edición de un Elemento
 * por un Administrador o Moderador.
 * (Peticiones 8 y 15).
 * --- ¡ACTUALIZADO (Sprint 10 / Relaciones)! ---
 */
public class ElementoFormDTO {

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255)
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 5000)
    private String descripcion;
    
    @NotBlank(message = "El tipo no puede estar vacío")
    private String tipoNombre; // (Ej. "Serie", "Libro")
    
    @NotBlank(message = "Los géneros no pueden estar vacíos")
    private String generosNombres; // (Ej. "Ciencia Ficción, Drama")
    
    @Size(max = 255)
    private String urlImagen;
    
    // --- Campos de Progreso ---
    @Size(max = 255)
    private String episodiosPorTemporada; // Para Series (ej. "10,8,12")
    @Min(value = 1)
    private Integer totalUnidades; // Para Anime / Manga
    @Min(value = 1)
    private Integer totalCapitulosLibro; // Para Libros
    @Min(value = 1)
    private Integer totalPaginasLibro; // Para Libros

    // --- ¡NUEVO CAMPO DE RELACIONES! (Añadido por Gemini) ---
    /**
     * Lista de IDs de los elementos que son SECUELAS de este.
     * (Es decir, los elementos que van DESPUÉS de este).
     */
    private List<Long> secuelaIds;
    // --- FIN DE CAMPO AÑADIDO ---
    
    // --- Getters y Setters ---
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { this.tipoNombre = tipoNombre; }
    public String getGenerosNombres() { return generosNombres; }
    public void setGenerosNombres(String generosNombres) { this.generosNombres = generosNombres; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    public String getEpisodiosPorTemporada() { return episodiosPorTemporada; }
    public void setEpisodiosPorTemporada(String episodiosPorTemporada) { this.episodiosPorTemporada = episodiosPorTemporada; }
    public Integer getTotalUnidades() { return totalUnidades; }
    public void setTotalUnidades(Integer totalUnidades) { this.totalUnidades = totalUnidades; }
    public Integer getTotalCapitulosLibro() { return totalCapitulosLibro; }
    public void setTotalCapitulosLibro(Integer totalCapitulosLibro) { this.totalCapitulosLibro = totalCapitulosLibro; }
    public Integer getTotalPaginasLibro() { return totalPaginasLibro; }
    public void setTotalPaginasLibro(Integer totalPaginasLibro) { this.totalPaginasLibro = totalPaginasLibro; }

    // --- ¡NUEVO GETTER/SETTER! ---
    public List<Long> getSecuelaIds() { return secuelaIds; }
    public void setSecuelaIds(List<Long> secuelaIds) { this.secuelaIds = secuelaIds; }
}