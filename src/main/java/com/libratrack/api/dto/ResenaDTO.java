package com.libratrack.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * (Refactorizado por Seguridad)
 * DTO (Data Transfer Object) para la PETICIÓN de CREAR una nueva Reseña (RF12).
 * Este es el "molde" del JSON que la app Flutter enviará.
 *
 * Seguridad (Mejor Práctica):
 * Se ha eliminado el campo 'usuarioId' de este DTO.
 * El autor de la reseña (el 'usuario') se obtendrá *exclusivamente*
 * del token JWT (el objeto 'Principal') en la capa del Controlador (ResenaController).
 * Esto previene que un usuario pueda publicar una reseña en nombre de otro.
 */
public class ResenaDTO {

    /**
     * El ID del Elemento que se está reseñando.
     * Es @NotNull porque una reseña no puede existir sin un elemento.
     */
    @NotNull(message = "El ID del elemento no puede ser nulo")
    private Long elementoId;

    /**
     * La valoración (ej. 1-5 estrellas) que da el usuario (RF12).
     */
    @NotNull(message = "La valoración no puede ser nula")
    @Min(value = 1, message = "La valoración mínima es 1")
    @Max(value = 5, message = "La valoración máxima es 5")
    private Integer valoracion;

    /**
     * El texto de la reseña (opcional) (RF12).
     * @Size valida que no exceda el límite de la BD.
     */
    @Size(max = 2000, message = "La reseña no puede exceder los 2000 caracteres")
    private String textoResena;

    
    // --- Getters y Setters ---
    // Necesarios para que Spring/Jackson pueda mapear el JSON
    // entrante (en una petición POST) a este objeto Java.

    public Long getElementoId() {
        return elementoId;
    }

    public void setElementoId(Long elementoId) {
        this.elementoId = elementoId;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public String getTextoResena() {
        return textoResena;
    }

    public void setTextoResena(String textoResena) {
        this.textoResena = textoResena;
    }
}