package com.libratrack.api.entity;

import com.libratrack.api.model.EstadoPersonal; // Importa tu Enum
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime; // Para la fecha en que se añadió

/**
 * Entidad que representa la tabla 'catalogo_personal'.
 * Esta es la "tabla pivote" (o de unión) que conecta a un Usuario
 * con un Elemento.
 * * Almacena los datos únicos de esa relación, como el estado y el progreso.
 * Implementa los requisitos RF05, RF06, RF07.
 */
@Entity
@Table(name = "catalogo_personal",
    // ¡Buena práctica! Añadimos una restricción a nivel de tabla.
    // Esto crea una clave única (unique key) en la BD para la combinación
    // de 'usuario_id' y 'elemento_id', asegurando que un usuario NO pueda
    // añadir el mismo elemento dos veces a su catálogo.
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "elemento_id"})
    }
)
public class CatalogoPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===================================================================
    // RELACIONES
    // ===================================================================

    /**
     * RELACIÓN 1: El Usuario
     * Muchas entradas del catálogo (ej: "mi entrada de Naruto", "mi entrada de Dune")
     * pertenecen a Un solo Usuario.
     * Esta es la clave FK 'usuario_id'.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) // Define la columna FK
    @NotNull
    private Usuario usuario;

    /**
     * RELACIÓN 2: El Elemento
     * Muchas entradas del catálogo (ej: "mi entrada de Dune", "la entrada de Dune de otro usuario")
     * apuntan a Un solo Elemento.
     * Esta es la clave FK 'elemento_id'.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elemento_id", nullable = false) // Define la columna FK
    @NotNull
    private Elemento elemento;

    // ===================================================================
    // DATOS EXTRA DE LA RELACIÓN (RF06, RF07)
    // ===================================================================

    /**
     * El estado de seguimiento personal del usuario (RF06).
     * Por defecto, cuando se añade un elemento, se marca como "PENDIENTE".
     */
    @Enumerated(EnumType.STRING) // Guarda "EN_PROGRESO" en lugar de un número
    @Column(nullable = false)
    @NotNull
    private EstadoPersonal estadoPersonal = EstadoPersonal.PENDIENTE; // Valor por defecto

    /**
     * El progreso específico que el usuario registra (RF07).
     * (Ej: "T4:E3", "Cap. 7", "Pág. 120").
     */
    @Size(max = 100, message = "El progreso no puede exceder los 100 caracteres")
    @Column(length = 100)
    private String progresoEspecifico;

    /**
     * Marca de tiempo de cuándo se añadió el elemento al catálogo.
     * 'updatable = false' asegura que esta fecha no se pueda modificar
     * después de la creación.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime agregadoEn;

    // --- Métodos de ciclo de vida ---

    /**
     * Este método se ejecuta automáticamente ANTES de que una nueva
     * entrada se guarde en la base de datos por primera vez.
     * Se usa para establecer la marca de tiempo 'agregadoEn'.
     */
    @PrePersist 
    protected void onCrear() {
        this.agregadoEn = LocalDateTime.now();
    }

    // --- Constructores ---

    public CatalogoPersonal() {
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

    public EstadoPersonal getEstadoPersonal() {
        return estadoPersonal;
    }

    public void setEstadoPersonal(EstadoPersonal estadoPersonal) {
        this.estadoPersonal = estadoPersonal;
    }

    public String getProgresoEspecifico() {
        return progresoEspecifico;
    }

    public void setProgresoEspecifico(String progresoEspecifico) {
        this.progresoEspecifico = progresoEspecifico;
    }

    public LocalDateTime getAgregadoEn() {
        return agregadoEn;
    }

    public void setAgregadoEn(LocalDateTime agregadoEn) {
        this.agregadoEn = agregadoEn;
    }
}