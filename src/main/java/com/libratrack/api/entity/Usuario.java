// Archivo: src/main/java/com/libratrack/api/entity/Usuario.java
package com.libratrack.api.entity;

import jakarta.persistence.*; 
import jakarta.validation.constraints.Email; 
import jakarta.validation.constraints.NotBlank; 
import jakarta.validation.constraints.Size; 

/**
 * --- ¡ACTUALIZADO (Sprint 4)! ---
 * --- ¡ACTUALIZADO (ID: QA-010)! Se añaden métodos helper de roles ---
 */
@Entity 
@Table(name = "usuarios") 
public class Usuario {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    // ... (campos username, email, password sin cambios) ...
    @Column(unique = true, nullable = false, length = 50) 
    @NotBlank(message = "El nombre de usuario no puede estar vacío") 
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un formato de email válido") 
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña no puede estar vacío")
    private String password; 

    @Column(nullable = false)
    private Boolean esModerador = false; 
    
    // --- ¡NUEVO CAMPO AÑADIDO! (Petición 13) ---
    @Column(nullable = false)
    private Boolean esAdministrador = false; // Por defecto 'false'

    @Column(length = 255) 
    private String fotoPerfilUrl;
    
    // --- Constructores ---
    public Usuario() {
    }

    public Usuario(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        // esModerador y esAdministrador son 'false' por defecto
    }
    
    // --- Getters y Setters ---

    // ... (getters/setters de id, username, email, password, esModerador, fotoPerfilUrl sin cambios) ...
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
    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public void setFotoPerfilUrl(String fotoPerfilUrl) { this.fotoPerfilUrl = fotoPerfilUrl; }
    
    // --- ¡NUEVO GETTER/SETTER! ---
    public Boolean getEsAdministrador() { return esAdministrador; }
    public void setEsAdministrador(Boolean esAdministrador) { this.esAdministrador = esAdministrador; }


    // --- ¡MÉTODOS HELPER AÑADIDOS! (ID: QA-010) ---
    // --- Esta es ahora la "fuente única de la verdad" para los roles ---

    /**
     * Método helper para comprobar el rol de Administrador (gestiona nulos).
     */
    @Transient // Le dice a JPA que no intente mapear esto a una columna
    public boolean esAdmin() {
        return this.esAdministrador != null && this.esAdministrador;
    }

    /**
     * Método helper para comprobar el rol de Moderador (incluye lógica de Admin).
     */
    @Transient // Le dice a JPA que no intente mapear esto a una columna
    public boolean esMod() {
        // (Petición 16) Un Admin es implícitamente un Moderador
        if (esAdmin()) {
            return true;
        }
        // Si no es Admin, comprueba su flag de moderador (gestionando nulos)
        return this.esModerador != null && this.esModerador;
    }
}