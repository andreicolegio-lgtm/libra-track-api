package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoContenido; // Importa tu Enum
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate; // Para la fecha de lanzamiento
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "elementos")
public class Elemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255)
    private String titulo;

    @Lob // "Large Object Binary" - Indica que será un campo de texto largo (TEXT)
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private LocalDate fechaLanzamiento;

    @Column(length = 500) // URL de la imagen
    private String imagenPortadaUrl;

    @Enumerated(EnumType.STRING) // Guarda el Enum como texto ("OFICIAL") y no como un número
    @Column(nullable = false)
    @NotNull
    private EstadoContenido estadoContenido = EstadoContenido.COMUNITARIO; // Valor por defecto

    // ===================================================================
    // RELACIONES (¡LO MÁS IMPORTANTE!)
    // ===================================================================

    // --- Relación 1-a-N con Tipo ---
    // (Muchos Elementos pertenecen a Un Tipo)
    @ManyToOne(fetch = FetchType.LAZY) // LAZY = Carga este dato solo cuando se pida
    @JoinColumn(name = "tipo_id", nullable = false) // Esta es la columna FK en la tabla 'elementos'
    @NotNull
    private Tipo tipo;

    // --- Relación N-a-M con Genero ---
    // (Muchos Elementos tienen Muchos Géneros)
    // Esto crea la tabla pivote 'elemento_genero' automáticamente [cite: 50]
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "elemento_genero", // Nombre de la tabla pivote
        joinColumns = @JoinColumn(name = "elemento_id"), // Columna que nos referencia
        inverseJoinColumns = @JoinColumn(name = "genero_id") // Columna que referencia a la otra entidad
    )
    private Set<Genero> generos = new HashSet<>();

    // --- Relación 1-a-N con Usuario (Proponente) ---
    // (Muchos Elementos son propuestos por Un Usuario)
    // Esto es para cumplir con RF13 [cite: 29]
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id") // El usuario que propuso este elemento (puede ser nulo si es 'Oficial')
    private Usuario creador;
    
    // --- Getters y Setters ---
    // (VS Code puede autogenerarlos, pero aquí están por claridad)

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