package com.libratrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO (Data Transfer Object) para la petición de CREAR un nuevo Elemento.
 *
 * Este es el "molde" de datos que el PropuestaElementoService (al aprobar)
 * o un AdminController (al crear directamente) usaría para pasar la
 * información necesaria al ElementoService.
 *
 * Contiene los IDs de las entidades relacionadas (Tipo, Usuario, Genero),
 * ya que el frontend (o el servicio que llama) no tiene por qué
 * conocer las entidades completas de JPA.
 */
public class ElementoDTO {

    /**
     * El título del contenido (RF10).
     * @NotBlank asegura que el string no sea nulo ni esté vacío.
     */
    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255)
    private String titulo;

    /**
     * La descripción (sinopsis). Es opcional.
     */
    private String descripcion;

    /**
     * La fecha de lanzamiento. Es opcional.
     */
    private LocalDate fechaLanzamiento;

    /**
     * La URL de la imagen de portada. Es opcional.
     */
    private String imagenPortadaUrl;

    /**
     * El ID de la entidad 'Tipo' a la que este Elemento pertenece.
     * @NotNull asegura que el ID debe ser proporcionado.
     */
    @NotNull(message = "El tipoId no puede ser nulo")
    private Long tipoId;

    /**
     * El ID del 'Usuario' que creó/propuso este Elemento (RF13).
     */
    @NotNull(message = "El creadorId no puede ser nulo")
    private Long creadorId;

    /**
     * Un conjunto (Set) de IDs de las entidades 'Genero'
     * a las que este Elemento pertenece (RF10).
     * @NotEmpty asegura que la lista de géneros no esté vacía.
     */
    @NotEmpty(message = "La lista de IDs de género no puede estar vacía")
    private Set<Long> generoIds;

    
    // --- Getters y Setters ---
    // Necesarios para que Spring/Jackson pueda mapear el JSON
    // entrante (en una petición POST o PUT) a este objeto Java.

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