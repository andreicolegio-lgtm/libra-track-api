// Archivo: src/main/java/com/libratrack/api/entity/RefreshToken.java
// (¡NUEVO ARCHIVO!)

package com.libratrack.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Entidad que representa la tabla 'refresh_tokens'.
 * Implementa la lógica de persistencia para los Tokens de Refresco.
 *
 * Sigue la práctica profesional (Opción C):
 * - Relación @ManyToOne con Usuario (Un usuario puede tener MÚLTIPLES tokens de refresco,
 * uno por cada dispositivo/sesión activa).
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * El usuario al que pertenece este token.
     * Mapeado como 'ManyToOne' (Muchos tokens pertenecen a Un usuario).
     * 'fetch = FetchType.LAZY' es una optimización para no cargar el
     * objeto Usuario completo a menos que se acceda a él explícitamente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull
    private Usuario usuario;

    /**
     * El token en sí (un UUID aleatorio).
     * Lo hacemos 'unique = true' para que la base de datos impida
     * colisiones accidentales y nos permita buscar por él rápidamente.
     */
    @Column(nullable = false, unique = true)
    @NotBlank
    private String token;

    /**
     * La fecha y hora (en UTC) en la que este token caducará.
     */
    @Column(nullable = false)
    @NotNull
    private Instant fechaExpiracion;

    
    // --- Constructores ---
    
    public RefreshToken() {
    }

    // --- Getters y Setters ---

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(Instant fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
}