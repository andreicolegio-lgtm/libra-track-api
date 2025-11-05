package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Entidad que representa la tabla 'resenas' en la base de datos.
 * Esta es una entidad de relación N-a-M entre Usuario y Elemento
 * que tiene sus propios atributos (valoración y texto).
 * Implementa el requisito RF12.
 */
@Entity
@Table(name = "resenas",
    // Restricción a nivel de BD para asegurar que un Usuario
    // solo pueda reseñar UN Elemento una sola vez.
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "elemento_id"})
    }
)
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===================================================================
    // RELACIONES
    // ===================================================================

    /**
     * RELACIÓN 1: El Usuario (Autor)
     * Muchas reseñas pertenecen a Un solo Usuario.
     * Esta es la clave FK 'usuario_id'.
     */
    @ManyToOne(fetch = FetchType.LAZY) // Carga perezosa para optimización
    @JoinColumn(name = "usuario_id", nullable = false) // Define la columna FK
    @NotNull
    private Usuario usuario;

    /**
     * RELACIÓN 2: El Elemento (Reseñado)
     * Muchas reseñas apuntan a Un solo Elemento.
     * Esta es la clave FK 'elemento_id'.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elemento_id", nullable = false) // Define la columna FK
    @NotNull
    private Elemento elemento;

    // ===================================================================
    // ATRIBUTOS PROPIOS DE LA RESEÑA (RF12)
    // ===================================================================

    /**
     * La valoración numérica (ej. 1-5 estrellas) que da el usuario.
     */
    @Min(value = 1, message = "La valoración mínima es 1")
    @Max(value = 5, message = "La valoración máxima es 5")
    @Column(nullable = false)
    @NotNull
    private Integer valoracion;

    /**
     * El texto de la reseña (opcional).
     * @Lob (Large Object) se mapea a 'TEXT' en MySQL.
     */
    @Size(max = 2000, message = "La reseña no puede exceder los 2000 caracteres")
    @Lob 
    private String textoResena;

    /**
     * Marca de tiempo de cuándo se creó la reseña.
     * 'updatable = false' asegura que no se pueda modificar.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // --- Métodos de ciclo de vida ---

    /**
     * Este método se ejecuta automáticamente ANTES de que una nueva
     * reseña se guarde en la base de datos por primera vez.
     * Se usa para establecer la marca de tiempo 'fechaCreacion'.
     */
    @PrePersist
    protected void onCrear() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // --- Constructores ---

    public Resena() {
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public String getTextoResena() {
        return textoResena;
    }

    public void setTextoResena(String textoResena) {
        this.textoResena = textoResena;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}