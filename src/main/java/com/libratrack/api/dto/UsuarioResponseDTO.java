// Archivo: src/main/java/com/libratrack/api/dto/UsuarioResponseDTO.java
package com.libratrack.api.dto;

import com.libratrack.api.entity.Usuario;

/**
 * DTO para enviar la información del perfil del usuario al cliente (Flutter).
 * --- ¡ACTUALIZADO (Sprint 4 - Corrección)! ---
 * --- ¡ACTUALIZADO (ID: QA-010)! Refactorizado para usar lógica centralizada de roles ---
 */
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String fotoPerfilUrl;
    
    private boolean esModerador;
    private boolean esAdministrador;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.fotoPerfilUrl = usuario.getFotoPerfilUrl();
        
        // --- ¡LÓGICA CORREGIDA Y CENTRALIZADA! (ID: QA-010) ---
        // Se usan los métodos helper de la entidad Usuario
        this.esAdministrador = usuario.esAdmin(); // <-- REFACTORIZADO
        this.esModerador = usuario.esMod();     // <-- REFACTORIZADO
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFotoPerfilUrl() { return fotoPerfilUrl; }

    // (Corregidos en 20251108-A61)
    public boolean isEsModerador() { return esModerador; }
    public boolean isEsAdministrador() { return esAdministrador; }
}