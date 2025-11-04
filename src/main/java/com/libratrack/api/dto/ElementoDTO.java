package com.libratrack.api.dto;

import java.time.LocalDate;
import java.util.Set;

/**
 * Data Transfer Object para crear un nuevo Elemento.
 * Este es el "molde" del JSON que la app móvil enviará.
 */
public class ElementoDTO {

    // Datos básicos
    private String titulo;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private String imagenPortadaUrl;

    // IDs de las relaciones
    private Long tipoId; // El ID del Tipo (ej: 1 para "Serie")
    private Long creadorId; // El ID del Usuario que lo propone (RF13)
    private Set<Long> generoIds; // Una lista de IDs de Géneros (ej: [1, 5])

    // --- Getters y Setters ---
    // (Spring los necesita para rellenar el objeto desde el JSON)

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(LocalDate fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }

    public String getImagenPortadaUrl() { return imagenPortadaUrl; }
    public void setImagenPortadaUrl(String imagenPortadaUrl) { this.imagenPortadaUrl = imagenPortadaUrl; }

    public Long getTipoId() { return tipoId; }
    public void setTipoId(Long tipoId) { this.tipoId = tipoId; }

    public Long getCreadorId() { return creadorId; }
    public void setCreadorId(Long creadorId) { this.creadorId = creadorId; }

    public Set<Long> getGeneroIds() { return generoIds; }
    public void setGeneroIds(Set<Long> generoIds) { this.generoIds = generoIds; }
}