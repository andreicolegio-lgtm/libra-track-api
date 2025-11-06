package com.libratrack.api.dto;

import com.libratrack.api.entity.Tipo;

/**
 * DTO (Data Transfer Object) de Lectura para la entidad Tipo (RF09).
 * * Propósito: Desacoplar la entidad JPA de la respuesta JSON para 
 * evitar la exposición de campos internos de la BD.
 */
public class TipoResponseDTO {

    private Long id;
    private String nombre;

    /**
     * Constructor que mapea la entidad Tipo al DTO.
     */
    public TipoResponseDTO(Tipo tipo) {
        this.id = tipo.getId();
        this.nombre = tipo.getNombre();
    }

    // --- Getters ---
    // Son necesarios para que Spring/Jackson construya el JSON.

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}