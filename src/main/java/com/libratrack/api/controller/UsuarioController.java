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
import java.util.Map; // <-- ¡NUEVA IMPORTACIÓN!

/**
 * --- ¡ACTUALIZADO (Sprint 3)! ---
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para obtener el perfil del usuario autenticado (GET /api/usuarios/me).
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> getMiPerfil(Principal principal) {
        String username = principal.getName();
        UsuarioResponseDTO perfil = usuarioService.getMiPerfil(username);
        return ResponseEntity.ok(perfil);
    }

    /**
     * Endpoint para actualizar el username del usuario (PUT /api/usuarios/me).
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> updateMiPerfil(Principal principal, @Valid @RequestBody UsuarioUpdateDTO updateDto) {
        String usernameActual = principal.getName();
        UsuarioResponseDTO perfilActualizado = usuarioService.updateMiPerfil(usernameActual, updateDto);
        return ResponseEntity.ok(perfilActualizado);
    }

    /**
     * Endpoint para cambiar la contraseña del usuario actual.
     */
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateMyPassword(Principal principal, @Valid @RequestBody PasswordChangeDTO passwordDto) {
        String usernameActual = principal.getName();
        usuarioService.changePassword(usernameActual, passwordDto);
        return ResponseEntity.ok().body("Contraseña actualizada con éxito.");
    }
    
    // --- ¡NUEVO ENDPOINT! (Petición 6) ---
    /**
     * Endpoint para actualizar la foto de perfil del usuario.
     * Recibe un JSON simple: {"url": "https://storage.googleapis.com/..."}
     */
    @PutMapping("/me/foto")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> updateFotoPerfil(
            Principal principal, 
            @RequestBody Map<String, String> body) {
        
        String username = principal.getName();
        String fotoUrl = body.get("url");
        
        if (fotoUrl == null || fotoUrl.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        UsuarioResponseDTO perfilActualizado = usuarioService.updateFotoPerfil(username, fotoUrl);
        return ResponseEntity.ok(perfilActualizado);
    }
}