// Archivo: src/main/java/com/libratrack/api/dto/ElementoResponseDTO.java
// (¡MODIFICADO POR GEMINI CON LA CORRECCIÓN COMPLETA!)

package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

// --- ¡NUEVA IMPORTACIÓN CRUCIAL! ---
import org.hibernate.Hibernate;
import java.util.Collections;
// ---

/**
 * DTO para ENVIAR los datos de un Elemento al cliente (Búsqueda/Detalle).
 * --- ¡ACTUALIZADO (Sprint 10 / Corrección LazyInitialization)! ---
 */
public class ElementoResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String urlImagen; 
    private LocalDate fechaLanzamiento; 
    private String tipoNombre; 
    private EstadoContenido estadoContenido;
    private String creadorUsername;
    private Set<String> generos;
    
    // --- ¡CAMPOS DE PROGRESO TOTAL REFACTORIZADOS! (Petición b, c, d) ---
    private EstadoPublicacion estadoPublicacion;
    private String episodiosPorTemporada; // Para Series
    private Integer totalUnidades;        // Para Anime / Manga
    private Integer totalCapitulosLibro;  // Para Libros
    private Integer totalPaginasLibro;    // Para Libros

    // --- Campos de Relaciones ---
    private Set<ElementoRelacionDTO> precuelas;
    private Set<ElementoRelacionDTO> secuelas;


    public ElementoResponseDTO(Elemento elemento) {
        
        // --- Campos Seguros (Primitivos o Enums) ---
        this.id = elemento.getId();
        this.titulo = elemento.getTitulo();
        this.descripcion = elemento.getDescripcion();
        this.urlImagen = elemento.getUrlImagen();
        this.fechaLanzamiento = elemento.getFechaLanzamiento(); 
        this.estadoContenido = elemento.getEstadoContenido();
        this.estadoPublicacion = elemento.getEstadoPublicacion();
        this.episodiosPorTemporada = elemento.getEpisodiosPorTemporada();
        this.totalUnidades = elemento.getTotalUnidades();
        this.totalCapitulosLibro = elemento.getTotalCapitulosLibro();
        this.totalPaginasLibro = elemento.getTotalPaginasLibro();
        
        // --- ¡MAPEO SEGURO DE TODAS LAS RELACIONES LAZY! ---
        
        // 1. Cargar Tipo (ManyToOne)
        if (Hibernate.isInitialized(elemento.getTipo()) && elemento.getTipo() != null) {
            this.tipoNombre = elemento.getTipo().getNombre();
        } else {
            this.tipoNombre = null; // O un valor por defecto si lo prefieres
        }

        // 2. Cargar Creador (ManyToOne)
        if (Hibernate.isInitialized(elemento.getCreador()) && elemento.getCreador() != null) {
            this.creadorUsername = elemento.getCreador().getUsername();
        } else {
            this.creadorUsername = "OFICIAL"; // El fallback que ya tenías
        }

        // 3. Cargar Generos (ManyToMany)
        if (Hibernate.isInitialized(elemento.getGeneros()) && elemento.getGeneros() != null) {
            this.generos = elemento.getGeneros().stream()
                .map(Genero::getNombre)
                .collect(Collectors.toSet());
        } else {
            this.generos = Collections.emptySet();
        }

        // 4. Cargar Precuelas (ManyToMany)
        if (Hibernate.isInitialized(elemento.getPrecuelas()) && elemento.getPrecuelas() != null) {
            this.precuelas = elemento.getPrecuelas().stream()
                .map(ElementoRelacionDTO::new)
                .collect(Collectors.toSet());
        } else {
            this.precuelas = Collections.emptySet(); 
        }
        
        // 5. Cargar Secuelas (ManyToMany)
        if (Hibernate.isInitialized(elemento.getSecuelas()) && elemento.getSecuelas() != null) {
            this.secuelas = elemento.getSecuelas().stream()
                .map(ElementoRelacionDTO::new)
                .collect(Collectors.toSet());
        } else {
            this.secuelas = Collections.emptySet(); 
        }
        // --- FIN DE LA CORRECCIÓN ---
    }

    // --- Getters ---
    // (Jackson los usa automáticamente)
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getUrlImagen() { return urlImagen; }
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public String getTipoNombre() { return tipoNombre; } 
    public EstadoContenido getEstadoContenido() { return estadoContenido; }
    public String getCreadorUsername() { return creadorUsername; }
    public Set<String> getGeneros() { return generos; }
    public EstadoPublicacion getEstadoPublicacion() { return estadoPublicacion; }
    
    public String getEpisodiosPorTemporada() { return episodiosPorTemporada; }
    public Integer getTotalUnidades() { return totalUnidades; }
    public Integer getTotalCapitulosLibro() { return totalCapitulosLibro; }
    public Integer getTotalPaginasLibro() { return totalPaginasLibro; }

    // Getters de Relaciones
    public Set<ElementoRelacionDTO> getPrecuelas() { return precuelas; }
    public Set<ElementoRelacionDTO> getSecuelas() { return secuelas; }
}