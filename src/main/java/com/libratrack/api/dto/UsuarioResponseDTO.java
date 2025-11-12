package com.libratrack.api.dto;

import com.libratrack.api.entity.Usuario;

/**
 * --- ¡ACTUALIZADO (Sprint 3)! ---
 */
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String rol;
    private String fotoPerfilUrl; // <-- ¡NUEVO CAMPO!

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.fotoPerfilUrl = usuario.getFotoPerfilUrl(); // <-- ¡NUEVO MAPEO!
        
        if (usuario.getEsModerador() != null && usuario.getEsModerador()) {
            this.rol = "ROLE_MODERADOR";
        } else {
            this.rol = "ROLE_USUARIO"; // Corregido (era ROLE_USUARIO)
        }
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public String getFotoPerfilUrl() { return fotoPerfilUrl; } // <-- ¡NUEVO GETTER!
}