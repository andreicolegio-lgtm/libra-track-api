package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO (Data Transfer Object) para RECIBIR datos al crear un Elemento
 * (usado internamente por el servicio de moderación).
 */
public class ElementoDTO {

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    // CORREGIDO: Nombres de campos
    private String urlImagen; 
    private LocalDate fechaLanzamiento;

    @NotNull(message = "El ID del Tipo no puede ser nulo")
    private Long tipoId; // Asumiendo que el servicio espera el ID del Tipo

    @NotNull(message = "El ID del Creador no puede ser nulo")
    private Long creadorId; // Asumiendo que el servicio espera el ID del Usuario

    @NotNull(message = "La lista de IDs de Géneros no puede ser nula")
    private Set<Long> generoIds; // Asumiendo que el servicio espera los IDs de Género

    // --- Getters y Setters ---

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    // CORREGIDO: Getters/Setters que coinciden con los errores
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(LocalDate fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }

    public Long getTipoId() { return tipoId; }
    public void setTipoId(Long tipoId) { this.tipoId = tipoId; }
    public Long getCreadorId() { return creadorId; }
    public void setCreadorId(Long creadorId) { this.creadorId = creadorId; }
    public Set<Long> getGeneroIds() { return generoIds; }
    public void setGeneroIds(Set<Long> generoIds) { this.generoIds = generoIds; }
}