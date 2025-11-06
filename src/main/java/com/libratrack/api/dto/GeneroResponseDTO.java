package com.libratrack.api.dto;

import com.libratrack.api.entity.Genero;

/**
 * DTO (Data Transfer Object) de Lectura para la entidad Genero (RF09).
 * * Propósito: Desacoplar la entidad JPA de la respuesta JSON.
 */
public class GeneroResponseDTO {

    private Long id;
    private String nombre;

    /**
     * Constructor que mapea la entidad Genero al DTO.
     */
    public GeneroResponseDTO(Genero genero) {
        this.id = genero.getId();
        this.nombre = genero.getNombre();
    }

    // --- Getters ---

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}