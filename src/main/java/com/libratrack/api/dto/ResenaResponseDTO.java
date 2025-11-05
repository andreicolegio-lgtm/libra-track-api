package com.libratrack.api.dto;

import com.libratrack.api.entity.Resena; // Importa la entidad Resena
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la RESPUESTA al ENVIAR una Reseña (RF12).
 *
 * Esta clase es crucial para la estabilidad de la API.
 * Su propósito es "aplanar" la entidad 'Resena', convirtiendo
 * las relaciones complejas (objetos 'Usuario' y 'Elemento')
 * en datos simples (un 'username' y un 'ID').
 *
 * Esto soluciona la 'LazyInitializationException' (Error 500)
 * que ocurre al intentar devolver una entidad JPA con relaciones 'LAZY'
 * (como 'Resena.getUsuario()') directamente desde un controlador.
 */
public class ResenaResponseDTO {

    // --- Campos de Datos Primitivos ---
    private Long id;
    private Integer valoracion;
    private String textoResena;
    private LocalDateTime fechaCreacion;
    
    // --- Relaciones Aplanadas ---
    
    /**
     * El ID del Elemento que fue reseñado.
     * (Aplanado desde la entidad 'Elemento' relacionada).
     */
    private Long elementoId;
    
    /**
     * El nombre de usuario del autor de la reseña.
     * (Aplanado desde la entidad 'Usuario' relacionada).
     */
    private String usernameAutor;

    
    // --- Constructor de Mapeo ---

    /**
     * Constructor especial que sabe cómo "mapear" (convertir)
     * una entidad 'Resena' (de la BD) a este DTO (para la respuesta JSON).
     *
     * Este constructor se llama desde el 'ResenaService' (mientras la
     * sesión de la BD sigue activa), por lo que puede acceder de forma
     * segura a las relaciones 'LAZY'.
     *
     * @param resena La entidad 'Resena' leída de la base de datos.
     */
    public ResenaResponseDTO(Resena resena) {
        // Mapeo 1 a 1 de campos simples
        this.id = resena.getId();
        this.valoracion = resena.getValoracion();
        this.textoResena = resena.getTextoResena();
        this.fechaCreacion = resena.getFechaCreacion();

        // Mapeo Seguro (Aplanado) de Relaciones LAZY
        
        // 1. Aplanar Elemento
        // Accede a la relación 'elemento' aquí y extrae solo el ID.
        this.elementoId = resena.getElemento().getId();
        
        // 2. Aplanar Usuario (Autor)
        // Accede a la relación 'usuario' aquí y extrae solo el 'username'.
        this.usernameAutor = resena.getUsuario().getUsername();
    }

    
    // --- Getters ---
    // Necesarios para que Spring/Jackson pueda leer los valores
    // y construir el JSON de respuesta.

    public Long getId() { return id; }
    public Integer getValoracion() { return valoracion; }
    public String getTextoResena() { return textoResena; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Long getElementoId() { return elementoId; }
    public String getUsernameAutor() { return usernameAutor; }
}