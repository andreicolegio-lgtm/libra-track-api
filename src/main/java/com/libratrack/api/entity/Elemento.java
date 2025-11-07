package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.model.EstadoPublicacion;
// Se elimina la importación del Enum 'TipoElemento'
import jakarta.persistence.*;
import java.time.LocalDate; // Importación para fecha
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "elementos")
public class Elemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, length = 5000)
    private String descripcion;
    
    @Column(length = 255)
    private String urlImagen; // Nombre del campo (Punto 12)

    @Column
    private LocalDate fechaLanzamiento; // (Campo que faltaba)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id", nullable = true)
    private Usuario creador;
    
    // --- CORRECCIÓN ---
    // Vuelve a la relación N:1 con la ENTIDAD 'Tipo'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private Tipo tipo; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoContenido estadoContenido;
    
    // --- CAMPOS NUEVOS (Punto 11 y 6) ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoPublicacion estadoPublicacion;

    private Integer totalTemporadas;
    private Integer totalUnidades; 
    private Boolean esUnidadUnica; 
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "elemento_genero",
        joinColumns = @JoinColumn(name = "elemento_id"),
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private Set<Genero> generos = new HashSet<>();

    // --- Constructores ---
    public Elemento() {}

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; } // Setter correcto

    public LocalDate getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(LocalDate fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; } // Setter correcto

    public Usuario getCreador() { return creador; }
    public void setCreador(Usuario creador) { this.creador = creador; }
    
    // --- CORRECCIÓN ---
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public EstadoContenido getEstadoContenido() { return estadoContenido; }
    public void setEstadoContenido(EstadoContenido estadoContenido) { this.estadoContenido = estadoContenido; }
    public Set<Genero> getGeneros() { return generos; }
    public void setGeneros(Set<Genero> generos) { this.generos = generos; }
    
    public EstadoPublicacion getEstadoPublicacion() { return estadoPublicacion; }
    public void setEstadoPublicacion(EstadoPublicacion estadoPublicacion) { this.estadoPublicacion = estadoPublicacion; }
    public Integer getTotalTemporadas() { return totalTemporadas; }
    public void setTotalTemporadas(Integer totalTemporadas) { this.totalTemporadas = totalTemporadas; }
    public Integer getTotalUnidades() { return totalUnidades; }
    public void setTotalUnidades(Integer totalUnidades) { this.totalUnidades = totalUnidades; }
    public Boolean getEsUnidadUnica() { return esUnidadUnica; }
    public void setEsUnidadUnica(Boolean esUnidadUnica) { this.esUnidadUnica = esUnidadUnica; }
}