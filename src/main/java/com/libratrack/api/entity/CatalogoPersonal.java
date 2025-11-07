package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoPersonal;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalogo_personal")
public class CatalogoPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elemento_id", nullable = false)
    private Elemento elemento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoPersonal estadoPersonal;
    
    @Column(nullable = false)
    private LocalDateTime agregadoEn;

    // --- CAMPOS REEMPLAZADOS/MODIFICADOS (Punto 6) ---
    
    /**
     * Temporada actual en la que se encuentra el usuario.
     */
    private Integer temporadaActual; 

    /**
     * Episodio/Capítulo/Página actual en la que se encuentra el usuario.
     */
    private Integer unidadActual;

    // --- Constructores y Lifecycle Callbacks ---
    
    public CatalogoPersonal() {
        // Inicializar progreso a 0/1 por defecto (si no es persistido)
        this.temporadaActual = 1;
        this.unidadActual = 0;
    }
    
    @PrePersist
    protected void onCreate() {
        // Inicializar hora si no está
        if (agregadoEn == null) {
            agregadoEn = LocalDateTime.now();
        }
        // Inicializar progreso si no se establece
        if (this.temporadaActual == null) { this.temporadaActual = 1; }
        if (this.unidadActual == null) { this.unidadActual = 0; }
    }

    // --- Getters y Setters ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Elemento getElemento() { return elemento; }
    public void setElemento(Elemento elemento) { this.elemento = elemento; }
    public EstadoPersonal getEstadoPersonal() { return estadoPersonal; }
    public void setEstadoPersonal(EstadoPersonal estadoPersonal) { this.estadoPersonal = estadoPersonal; }
    public LocalDateTime getAgregadoEn() { return agregadoEn; }
    public void setAgregadoEn(LocalDateTime agregadoEn) { this.agregadoEn = agregadoEn; }
    
    // Getters y Setters de Progreso Actual
    public Integer getTemporadaActual() { return temporadaActual; }
    public void setTemporadaActual(Integer temporadaActual) { this.temporadaActual = temporadaActual; }
    public Integer getUnidadActual() { return unidadActual; }
    public void setUnidadActual(Integer unidadActual) { this.unidadActual = unidadActual; }
}