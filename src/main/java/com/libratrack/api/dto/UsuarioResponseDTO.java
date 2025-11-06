package com.libratrack.api.dto;

import com.libratrack.api.entity.Usuario;

/**
 * DTO (Data Transfer Object) para enviar la información del perfil del usuario
 * al cliente (Flutter) de forma segura.
 */
public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String rol;

    // --- CONSTRUCTOR CORREGIDO (Definitivo) ---
    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        
        // --- CORRECCIÓN ---
        // Leemos el campo booleano 'esModerador' directamente de la entidad
        // usando el getter 'getEsModerador()' que SÍ existe.
        //
        // Esta es la lógica que tu 'UserDetailsServiceImpl'
        // probablemente usa para asignar las autoridades.
        
        if (usuario.getEsModerador() != null && usuario.getEsModerador()) {
            // Si esModerador es true, el rol es MODERADOR
            this.rol = "ROLE_MODERADOR";
        } else {
            // Si es false o null, el rol es USUARIO
            this.rol = "ROLE_USUARIO";
        }
    }

    // --- Getters ---

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }
}