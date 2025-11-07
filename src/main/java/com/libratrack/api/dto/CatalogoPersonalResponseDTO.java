package com.libratrack.api.dto;

import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.model.EstadoPersonal;
import com.libratrack.api.model.EstadoPublicacion;
// Se elimina la importación del Enum 'TipoElemento'
import java.time.LocalDateTime;

/**
 * DTO para ENVIAR una entrada del catálogo al cliente (móvil).
 */
public class CatalogoPersonalResponseDTO {

    private Long id;
    private EstadoPersonal estadoPersonal;
    private LocalDateTime agregadoEn;
    
    // --- Progreso Actual (Punto 6) ---
    private Integer temporadaActual; 
    private Integer unidadActual;

    // --- Datos del Elemento Relacionado ---
    private Long elementoId;
    private String elementoTitulo;
    private String elementoTipoNombre; // <--- CORREGIDO: Ahora es un String

    // --- Progreso TOTAL del Elemento (Punto 6 y 11) ---
    private EstadoPublicacion elementoEstadoPublicacion; 
    private Integer elementoTotalTemporadas;
    private Integer elementoTotalUnidades;
    private Boolean elementoEsUnidadUnica;
    private String elementoUrlImagen;

    private Long usuarioId;

    // --- Constructor de Mapeo ---
    public CatalogoPersonalResponseDTO(CatalogoPersonal entrada) {
        this.id = entrada.getId();
        this.estadoPersonal = entrada.getEstadoPersonal();
        this.agregadoEn = entrada.getAgregadoEn();
        
        this.temporadaActual = entrada.getTemporadaActual(); 
        this.unidadActual = entrada.getUnidadActual();
        
        // --- CORRECCIÓN ---
        // Aplanamos la entidad 'Tipo' para obtener solo el nombre
        this.elementoId = entrada.getElemento().getId();
        this.elementoTitulo = entrada.getElemento().getTitulo();
        this.elementoTipoNombre = entrada.getElemento().getTipo().getNombre(); // <--- CORREGIDO
        
        this.elementoEstadoPublicacion = entrada.getElemento().getEstadoPublicacion();
        this.elementoTotalTemporadas = entrada.getElemento().getTotalTemporadas();
        this.elementoTotalUnidades = entrada.getElemento().getTotalUnidades();
        this.elementoEsUnidadUnica = entrada.getElemento().getEsUnidadUnica();
        this.elementoUrlImagen = entrada.getElemento().getUrlImagen();
        
        this.usuarioId = entrada.getUsuario().getId();
    }

    // --- Getters ---

    public Long getId() { return id; }
    public EstadoPersonal getEstadoPersonal() { return estadoPersonal; }
    public LocalDateTime getAgregadoEn() { return agregadoEn; }
    
    public Integer getTemporadaActual() { return temporadaActual; }
    public Integer getUnidadActual() { return unidadActual; }

    public Long getElementoId() { return elementoId; }
    public String getElementoTitulo() { return elementoTitulo; }
    public String getElementoTipoNombre() { return elementoTipoNombre; } // <--- CORREGIDO
    
    public EstadoPublicacion getElementoEstadoPublicacion() { return elementoEstadoPublicacion; }
    public Integer getElementoTotalTemporadas() { return elementoTotalTemporadas; }
    public Integer getElementoTotalUnidades() { return elementoTotalUnidades; }
    public Boolean getElementoEsUnidadUnica() { return elementoEsUnidadUnica; } // Corregido el typo
    public String getElementoUrlImagen() { return elementoUrlImagen; }

    public Long getUsuarioId() { return usuarioId; }
}