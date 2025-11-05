package com.libratrack.api.dto;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.model.EstadoContenido;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors; // Import para 'streams' y 'Collectors'

/**
 * DTO (Data Transfer Object) para la RESPUESTA al ENVIAR un Elemento.
 *
 * Esta clase es crucial para la estabilidad de la API.
 * Su propósito es "aplanar" la entidad 'Elemento', convirtiendo
 * las relaciones complejas (objetos 'Tipo', 'Genero', 'Usuario')
 * en datos simples (Strings, IDs).
 *
 * Esto soluciona la 'LazyInitializationException' (Error 500)
 * que ocurre al intentar devolver una entidad JPA con relaciones 'LAZY'
 * directamente desde un controlador.
 */
public class ElementoResponseDTO {

    // --- Campos de Datos Primitivos ---
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private String imagenPortadaUrl;
    private EstadoContenido estadoContenido;

    // --- Relaciones Aplanadas ---
    // En lugar de devolver el objeto 'Tipo' completo,
    // solo devolvemos el 'String' del nombre.
    private String tipo;

    // En lugar de devolver un Set<Genero>,
    // devolvemos un Set<String> con los nombres.
    private Set<String> generos;
    
    // Solo el nombre del creador, no la entidad Usuario completa.
    private String creadorUsername;

    
    // --- Constructor de Mapeo ---
    
    /**
     * Constructor especial que sabe cómo "mapear" (convertir)
     * una entidad 'Elemento' (de la BD) a este DTO (para la respuesta JSON).
     *
     * @param elemento La entidad 'Elemento' leída de la base de datos.
     */
    public ElementoResponseDTO(Elemento elemento) {
        // Mapeo 1 a 1 de campos simples
        this.id = elemento.getId();
        this.titulo = elemento.getTitulo();
        this.descripcion = elemento.getDescripcion();
        this.fechaLanzamiento = elemento.getFechaLanzamiento();
        this.imagenPortadaUrl = elemento.getImagenPortadaUrl();
        this.estadoContenido = elemento.getEstadoContenido();

        // Mapeo Seguro (Aplanado) de Relaciones LAZY
        
        // 1. Aplanar Tipo
        // Accede a la relación 'tipo' aquí (mientras la sesión de BD sigue activa)
        // y extrae solo el nombre.
        this.tipo = elemento.getTipo().getNombre();
        
        // 2. Aplanar Generos
        // Usa un 'stream' de Java para convertir el Set<Genero> en un Set<String>
        // (recorriendo la lista y extrayendo solo el nombre de cada género).
        this.generos = elemento.getGeneros().stream()
                            .map(genero -> genero.getNombre())
                            .collect(Collectors.toSet());

        // 3. Aplanar Creador (con comprobación de nulo)
        // El creador puede ser nulo si el contenido es OFICIAL (sin proponente).
        if (elemento.getCreador() != null) {
            this.creadorUsername = elemento.getCreador().getUsername();
        } else {
            this.creadorUsername = "Administrador"; // O null, según prefieras
        }
    }

    
    // --- Getters ---
    // Necesarios para que Spring/Jackson pueda leer los valores
    // y construir el JSON de respuesta.

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public String getImagenPortadaUrl() { return imagenPortadaUrl; }
    public EstadoContenido getEstadoContenido() { return estadoContenido; }
    public String getTipo() { return tipo; }
    public Set<String> getGeneros() { return generos; }
    public String getCreadorUsername() { return creadorUsername; }
}