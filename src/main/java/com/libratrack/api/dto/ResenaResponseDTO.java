package com.libratrack.api.dto;

import com.libratrack.api.entity.Resena;
import java.time.LocalDateTime;

/**
 * DTO para ENVIAR una reseña al cliente (móvil).
 * Esto evita el error LazyInitializationException.
 */
public class ResenaResponseDTO {

    private Long id;
    private Integer valoracion;
    private String textoResena;
    private LocalDateTime fechaCreacion;
    
    // Datos "planos" de las relaciones
    private Long elementoId;
    private String usernameAutor; // Solo mandamos el nombre, no el objeto Usuario

    // --- Constructor ---
    // Creamos un constructor que sabe cómo "mapear"
    // una Entidad Resena a este DTO.
    public ResenaResponseDTO(Resena resena) {
        this.id = resena.getId();
        this.valoracion = resena.getValoracion();
        this.textoResena = resena.getTextoResena();
        this.fechaCreacion = resena.getFechaCreacion();
        this.elementoId = resena.getElemento().getId();
        this.usernameAutor = resena.getUsuario().getUsername();
    }

    // --- Getters (Spring los necesita para el JSON) ---

    public Long getId() { return id; }
    public Integer getValoracion() { return valoracion; }
    public String getTextoResena() { return textoResena; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Long getElementoId() { return elementoId; }
    public String getUsernameAutor() { return usernameAutor; }
}