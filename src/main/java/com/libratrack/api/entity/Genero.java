package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidad que representa la tabla 'generos' en la base de datos.
 * Es una "tabla de consulta" (lookup table) para categorizar los Elementos.
 * (Ej: "Ciencia Ficción", "Drama", "Aventura").
 * Cumple el requisito RF10.
 */
@Entity
@Table(name = "generos")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * El nombre único del género.
     */
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "El nombre del género no puede estar vacío")
    @Size(max = 50)
    private String nombre;

    
    // --- Constructores ---

    public Genero() {
        // Constructor vacío requerido por JPA
    }

    /**
     * Constructor de conveniencia para crear un Genero con un nombre.
     * Usado en el servicio de moderación al "traducir" strings.
     */
    public Genero(String nombre) {
        this.nombre = nombre;
    }

    
    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}