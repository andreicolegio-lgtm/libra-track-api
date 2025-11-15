// Archivo: src/main/java/com/libratrack/api/dto/ElementoRelacionDTO.java
// (¡NUEVO ARCHIVO!)

package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;

/**
 * DTO "superficial" (shallow) usado para representar relaciones (precuelas/secuelas)
 * dentro de ElementoResponseDTO.
 *
 * Contiene solo los datos mínimos necesarios para que el frontend (Flutter)
 * pueda renderizar un enlace (título, imagen) y navegar a él (id).
 *
 * Esto es crucial para evitar bucles infinitos de serialización JSON.
 */
public class ElementoRelacionDTO {

    private Long id;
    private String titulo;
    private String urlImagen;

    /**
     * Constructor que mapea la entidad Elemento (completa)
     * a este DTO (simple).
     */
    public ElementoRelacionDTO(Elemento elemento) {
        this.id = elemento.getId();
        this.titulo = elemento.getTitulo();
        this.urlImagen = elemento.getUrlImagen();
    }

    // --- Getters ---
    // (Jackson los usa para construir el JSON)

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrlImagen() {
        return urlImagen;
    }
}