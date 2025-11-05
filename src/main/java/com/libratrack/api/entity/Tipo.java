package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidad que representa la tabla 'tipos' en la base de datos.
 * Es una "tabla de consulta" (lookup table) para categorizar los Elementos.
 * (Ej: "Serie", "Libro", "Película", "Videojuego").
 * Cumple el requisito RF10[cite: 28].
 */
@Entity
@Table(name = "tipos")
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * El nombre único del tipo de contenido.
     */
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "El nombre del tipo no puede estar vacío")
    @Size(max = 50)
    private String nombre;

    
    // --- Constructores ---

    public Tipo() {
        // Constructor vacío requerido por JPA
    }

    /**
     * Constructor de conveniencia para crear un Tipo con un nombre.
     * Usado en el servicio de moderación al "traducir" strings.
     */
    public Tipo(String nombre) {
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