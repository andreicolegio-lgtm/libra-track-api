package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
// Se elimina la importación del Enum 'TipoElemento'
import java.time.LocalDate; // <-- NUEVA IMPORTACIÓN
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO para ENVIAR los datos de un Elemento al cliente (Búsqueda/Detalle).
 */
public class ElementoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String urlImagen; 
    private LocalDate fechaLanzamiento; // <-- NUEVO CAMPO AÑADIDO
    private String tipoNombre; // <--- CORREGIDO: String en lugar de TipoElemento
    private EstadoContenido estadoContenido;
    private String creadorUsername;
    private Set<String> generos;
    
    // Campos de Progreso Total
    private EstadoPublicacion estadoPublicacion;
    private Integer totalTemporadas;
    private Integer totalUnidades;
    private Boolean esUnidadUnica;


    public ElementoResponseDTO(Elemento elemento) {
        this.id = elemento.getId();
        this.titulo = elemento.getTitulo();
        this.descripcion = elemento.getDescripcion();
        this.urlImagen = elemento.getUrlImagen();
        this.fechaLanzamiento = elemento.getFechaLanzamiento(); // <-- NUEVO
        this.tipoNombre = elemento.getTipo().getNombre(); // <--- CORREGIDO
        this.estadoContenido = elemento.getEstadoContenido();
        this.creadorUsername = elemento.getCreador() != null ? elemento.getCreador().getUsername() : "OFICIAL";
        
        this.generos = elemento.getGeneros().stream()
            .map(Genero::getNombre)
            .collect(Collectors.toSet());
            
        this.estadoPublicacion = elemento.getEstadoPublicacion();
        this.totalTemporadas = elemento.getTotalTemporadas();
        this.totalUnidades = elemento.getTotalUnidades();
        this.esUnidadUnica = elemento.getEsUnidadUnica();
    }

    // --- Getters ---

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getUrlImagen() { return urlImagen; }
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; } // <-- NUEVO
    public String getTipoNombre() { return tipoNombre; } // <--- CORREGIDO
    public EstadoContenido getEstadoContenido() { return estadoContenido; }
    public String getCreadorUsername() { return creadorUsername; }
    public Set<String> getGeneros() { return generos; }
    
    public EstadoPublicacion getEstadoPublicacion() { return estadoPublicacion; }
    public Integer getTotalTemporadas() { return totalTemporadas; }
    public Integer getTotalUnidades() { return totalUnidades; }
    public Boolean getEsUnidadUnica() { return esUnidadUnica; } // Corregido el typo
}