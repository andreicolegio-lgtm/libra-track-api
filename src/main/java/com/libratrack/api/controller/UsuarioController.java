// Archivo: src/main/java/com/libratrack/api/controller/UsuarioController.java
package com.libratrack.api.controller;

import com.libratrack.api.dto.PasswordChangeDTO;
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
 * REFACTORIZADO: Eliminado try-catch manual.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ... (getMiPerfil sin cambios)

    /**
     * Endpoint para actualizar el username del usuario (PUT /api/usuarios/me).
     * REFACTORIZADO: Eliminado try-catch.
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> updateMiPerfil(Principal principal, @Valid @RequestBody UsuarioUpdateDTO updateDto) {
        
        String usernameActual = principal.getName();
        // El servicio lanza 404/409.
        UsuarioResponseDTO perfilActualizado = usuarioService.updateMiPerfil(usernameActual, updateDto);
        return ResponseEntity.ok(perfilActualizado);
    }

    // --- CAMBIO DE CONTRASEÑA (RF04) ---

    /**
     * Endpoint para cambiar la contraseña del usuario actual.
     * REFACTORIZADO: Eliminado try-catch.
     */
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateMyPassword(Principal principal, @Valid @RequestBody PasswordChangeDTO passwordDto) {
        
        String usernameActual = principal.getName();
        
        // El servicio lanza 404/409.
        usuarioService.changePassword(usernameActual, passwordDto);
        
        // Si no hay excepciones, la contraseña se cambió con éxito
        return ResponseEntity.ok().body("Contraseña actualizada con éxito.");
    }
}