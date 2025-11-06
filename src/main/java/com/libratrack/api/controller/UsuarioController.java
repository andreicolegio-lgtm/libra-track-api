package com.libratrack.api.controller;

import com.libratrack.api.dto.PasswordChangeDTO; // --- NUEVA IMPORTACIÓN ---
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.dto.UsuarioUpdateDTO; 
import com.libratrack.api.service.UsuarioService; 
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controlador REST para gestionar las operaciones del perfil de usuario (RF04).
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para obtener el perfil del usuario (GET /api/usuarios/me).
     * [Código 100% preservado]
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> getMiPerfil(Principal principal) {
        
        String username = principal.getName();
        UsuarioResponseDTO perfilDto = usuarioService.getMiPerfil(username);
        return ResponseEntity.ok(perfilDto);
    }

    /**
     * Endpoint para actualizar el username del usuario (PUT /api/usuarios/me).
     * [Código 100% preservado]
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMiPerfil(Principal principal, @Valid @RequestBody UsuarioUpdateDTO updateDto) {
        
        try {
            String usernameActual = principal.getName();
            UsuarioResponseDTO perfilActualizado = usuarioService.updateMiPerfil(usernameActual, updateDto);
            return ResponseEntity.ok(perfilActualizado);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- NUEVO ENDPOINT AÑADIDO (RF04 - Cambio de Contraseña) ---

    /**
     * Endpoint para cambiar la contraseña del usuario actual.
     * Escucha en: PUT /api/usuarios/me/password
     *
     * @param principal El usuario (del token) que hace la petición.
     * @param passwordDto El cuerpo (body) con la contraseña actual y la nueva.
     * @return 200 OK con un mensaje, o 400 Bad Request si la contraseña
     * actual es incorrecta.
     */
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMyPassword(Principal principal, @Valid @RequestBody PasswordChangeDTO passwordDto) {
        
        try {
            String usernameActual = principal.getName();
            
            // Llama al nuevo método del servicio
            usuarioService.changePassword(usernameActual, passwordDto);
            
            // Si no hay excepciones, la contraseña se cambió con éxito
            return ResponseEntity.ok().body("Contraseña actualizada con éxito.");
            
        } catch (Exception e) {
            // Capturamos el error (ej. "La contraseña actual es incorrecta.")
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}