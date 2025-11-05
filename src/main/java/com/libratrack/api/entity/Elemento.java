package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoContenido; // Importa tu Enum
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate; // Para la fecha de lanzamiento
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad central de LibraTrack. Representa la tabla 'elementos'.
 * Esta es la tabla principal que verán los usuarios (RF09, RF10).
 * Contiene todo el contenido "limpio" (aprobado), ya sea OFICIAL (RF16) o COMUNITARIO.
 */
@Entity
@Table(name = "elementos")
public class Elemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título del contenido.
     */
    @Column(nullable = false)
    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255)
    private String titulo;

    /**
     * Descripción o sinopsis del contenido.
     * @Lob (Large Object) se mapea a un tipo de dato 'TEXT' en MySQL,
     * permitiendo descripciones muy largas.
     */
    @Lob 
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Fecha de lanzamiento original del contenido.
     */
    private LocalDate fechaLanzamiento;

    /**
     * URL a la imagen de portada/póster.
     */
    @Column(length = 500) 
    private String imagenPortadaUrl;

    /**
     * Define el estado del contenido (RF11, RF16).
     * @Enumerated(EnumType.STRING) guarda el texto ("OFICIAL") en la BD,
     * lo cual es más legible que un número (EnumType.ORDINAL).
     */
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    @NotNull
    private EstadoContenido estadoContenido; // RF16: OFICIAL o COMUNITARIO

    // ===================================================================
    // RELACIONES (¡LO MÁS IMPORTANTE!)
    // ===================================================================

    /**
     * RELACIÓN 1-a-N con Tipo.
     * Muchos Elementos pueden pertenecer a Un Tipo.
     * fetch = FetchType.LAZY es una optimización crucial:
     * le dice a JPA que NO cargue el objeto 'Tipo' hasta que
     * no lo pidamos explícitamente (ej. con un getTipo()).
     * Esto evita errores 'LazyInitializationException' (Error 500) en los controladores.
     */
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "tipo_id", nullable = false) // Define la columna FK 'tipo_id'
    @NotNull
    private Tipo tipo;

    /**
     * RELACIÓN N-a-M con Genero (RF10).
     * Muchos Elementos pueden tener Muchos Géneros.
     * JPA creará automáticamente la tabla pivote 'elemento_genero'
     * gracias a la anotación @JoinTable.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "elemento_genero", // Nombre de la tabla pivote
        joinColumns = @JoinColumn(name = "elemento_id"), // Columna FK de esta entidad
        inverseJoinColumns = @JoinColumn(name = "genero_id") // Columna FK de la otra entidad
    )
    private Set<Genero> generos = new HashSet<>(); // Usamos un 'Set' para evitar géneros duplicados

    /**
     * RELACIÓN 1-a-N con Usuario (Proponente).
     * Muchos Elementos (comunitarios) son propuestos por Un Usuario (RF13).
     * Si el contenido es OFICIAL, 'creador' puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id") 
    private Usuario creador;

    
    // --- Constructores ---

    public Elemento() {
        // Constructor vacío requerido por JPA
    }

    // --- Getters y Setters ---
    // Métodos públicos necesarios para que Spring (JPA y Jackson)
    // pueda leer y escribir en los campos privados de esta clase.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public String getImagenPortadaUrl() {
        return imagenPortadaUrl;
    }

    public void setImagenPortadaUrl(String imagenPortadaUrl) {
        this.imagenPortadaUrl = imagenPortadaUrl;
    }

    public EstadoContenido getEstadoContenido() {
        return estadoContenido;
    }

    public void setEstadoContenido(EstadoContenido estadoContenido) {
        this.estadoContenido = estadoContenido;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Set<Genero> getGeneros() {
        return generos;
    }

    public void setGeneros(Set<Genero> generos) {
        this.generos = generos;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }
}