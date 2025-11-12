package com.libratrack.api.dto;

import com.libratrack.api.entity.Resena; 
import java.time.LocalDateTime;

/**
 * --- ¡ACTUALIZADO (Sprint 3)! ---
 */
public class ResenaResponseDTO {

    // --- Campos de Datos Primitivos ---
    private Long id;
    private Integer valoracion;
    private String textoResena;
    private LocalDateTime fechaCreacion;
    
    // --- Relaciones Aplanadas ---
    private Long elementoId;
    private String usernameAutor;
    private String autorFotoPerfilUrl; // <-- ¡NUEVO CAMPO!

    
    // --- Constructor de Mapeo ---
    public ResenaResponseDTO(Resena resena) {
        this.id = resena.getId();
        this.valoracion = resena.getValoracion();
        this.textoResena = resena.getTextoResena();
        this.fechaCreacion = resena.getFechaCreacion();

        // Mapeo Seguro (Aplanado) de Relaciones LAZY
        this.elementoId = resena.getElemento().getId();
        this.usernameAutor = resena.getUsuario().getUsername();
        
        // --- ¡NUEVO MAPEO! ---
        // (Esto es seguro gracias al @Transactional en el Service)
        this.autorFotoPerfilUrl = resena.getUsuario().getFotoPerfilUrl();
    }

    
    // --- Getters ---
    public Long getId() { return id; }
    public Integer getValoracion() { return valoracion; }
    public String getTextoResena() { return textoResena; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Long getElementoId() { return elementoId; }
    public String getUsernameAutor() { return usernameAutor; }
    public String getAutorFotoPerfilUrl() { return autorFotoPerfilUrl; } // <-- ¡NUEVO GETTER!
}