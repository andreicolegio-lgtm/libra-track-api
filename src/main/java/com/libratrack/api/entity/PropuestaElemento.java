package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoPropuesta; // Importa tu Enum
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entidad que representa la tabla 'propuestas_elementos'.
 * Esta es la "sala de espera" o "cola de moderación".
 * Cuando un usuario propone contenido (RF13), se crea una fila aquí.
 * Los moderadores la revisan (RF14) y la aprueban o rechazan (RF15).
 */
@Entity
@Table(name = "propuestas_elementos")
public class PropuestaElemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===================================================================
    // RELACIONES
    // ===================================================================

    /**
     * RELACIÓN 1: El Proponente (RF13)
     * Muchas propuestas son creadas por Un Usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proponente_id", nullable = false) // FK a 'usuarios'
    private Usuario proponente;

    /**
     * RELACIÓN 2: El Revisor (RF15)
     * Muchas propuestas son gestionadas por Un Moderador.
     * Es 'nullable' (puede ser nulo) porque estará vacío
     * hasta que alguien la apruebe o rechace.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisor_id") // FK a 'usuarios'
    private Usuario revisor;

    // ===================================================================
    // DATOS SUGERIDOS POR EL USUARIO (RF13)
    // ===================================================================

    /**
     * El título que el usuario sugiere.
     */
    @Column(nullable = false)
    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 255)
    private String tituloSugerido;

    /**
     * La descripción que el usuario sugiere (mapeada a 'TEXT').
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcionSugerida;

    /**
     * El TIPO que el usuario sugiere (ej. "Serie", "Anime").
     * El moderador deberá "traducir" esto a una entidad 'Tipo' real.
     */
    @Size(max = 100)
    private String tipoSugerido;

    /**
     * Los GÉNEROS que el usuario sugiere (ej. "Ciencia Ficción, Drama").
     * El moderador deberá "traducir" esto.
     */
    @Size(max = 255)
    private String generosSugeridos;

    // ===================================================================
    // ESTADO DE MODERACIÓN (RF14, RF15)
    // ===================================================================

    /**
     * El estado actual de la propuesta en la cola.
     * Por defecto, siempre es "PENDIENTE" al crearse.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPropuesta estadoPropuesta = EstadoPropuesta.PENDIENTE;

    /**
     * Comentarios opcionales del moderador.
     * (Ej: "Rechazado por ser un duplicado del Elemento #123").
     */
    @Size(max = 500)
    private String comentariosRevision;

    /**
     * Marca de tiempo de cuándo se creó la propuesta.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaPropuesta;

    // --- Métodos de ciclo de vida ---

    /**
     * Este método se ejecuta automáticamente ANTES de que una nueva
     * propuesta se guarde en la base de datos por primera vez.
     * Se usa para establecer la marca de tiempo 'fechaPropuesta'.
     */
    @PrePersist
    protected void onCrear() {
        this.fechaPropuesta = LocalDateTime.now();
    }

    // --- Constructores ---

    public PropuestaElemento() {
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

    public Usuario getProponente() {
        return proponente;
    }

    public void setProponente(Usuario proponente) {
        this.proponente = proponente;
    }

    public String getTituloSugerido() {
        return tituloSugerido;
    }

    public void setTituloSugerido(String tituloSugerido) {
        this.tituloSugerido = tituloSugerido;
    }

    public String getDescripcionSugerida() {
        return descripcionSugerida;
    }

    public void setDescripcionSugerida(String descripcionSugerida) {
        this.descripcionSugerida = descripcionSugerida;
    }

    public String getTipoSugerido() {
        return tipoSugerido;
    }

    public void setTipoSugerido(String tipoSugerido) {
        this.tipoSugerido = tipoSugerido;
    }

    public String getGenerosSugeridos() {
        return generosSugeridos;
    }

    public void setGenerosSugeridos(String generosSugeridos) {
        this.generosSugeridos = generosSugeridos;
    }

    public EstadoPropuesta getEstadoPropuesta() {
        return estadoPropuesta;
    }

    public void setEstadoPropuesta(EstadoPropuesta estadoPropuesta) {
        this.estadoPropuesta = estadoPropuesta;
    }

    public Usuario getRevisor() {
        return revisor;
    }

    public void setRevisor(Usuario revisor) {
        this.revisor = revisor;
    }

    public String getComentariosRevision() {
        return comentariosRevision;
    }

    public void setComentariosRevision(String comentariosRevision) {
        this.comentariosRevision = comentariosRevision;
    }

    public LocalDateTime getFechaPropuesta() {
        return fechaPropuesta;
    }

    public void setFechaPropuesta(LocalDateTime fechaPropuesta) {
        this.fechaPropuesta = fechaPropuesta;
    }
}