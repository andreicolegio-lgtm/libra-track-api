package com.libratrack.api.entity;

import jakarta.persistence.*; 
import jakarta.validation.constraints.Email; 
import jakarta.validation.constraints.NotBlank; 
import jakarta.validation.constraints.Size; 

/**
 * --- ¡ACTUALIZADO (Sprint 3)! ---
 */
@Entity 
@Table(name = "usuarios") 
public class Usuario {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(unique = true, nullable = false, length = 50) 
    @NotBlank(message = "El nombre de usuario no puede estar vacío") 
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un formato de email válido") 
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password; 

    @Column(nullable = false)
    private Boolean esModerador = false; 

    // --- ¡NUEVO CAMPO AÑADIDO! (Petición 6) ---
    @Column(length = 255) // Para almacenar la URL de GCS
    private String fotoPerfilUrl;
    
    // --- Constructores ---
    public Usuario() {
    }

    public Usuario(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Boolean getEsModerador() { return esModerador; }
    public void setEsModerador(Boolean esModerador) { this.esModerador = esModerador; }

    // --- ¡NUEVO GETTER/SETTER! ---
    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }
}