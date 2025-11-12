// Archivo: src/main/java/com/libratrack/api/service/UsuarioService.java
package com.libratrack.api.service;

import com.libratrack.api.dto.PasswordChangeDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.dto.UsuarioUpdateDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException; 
import com.libratrack.api.exception.ResourceNotFoundException; 
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * --- ¡ACTUALIZADO (Sprint 3)! ---
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(Usuario nuevoUsuario) { 
        // ... (código sin cambios)
        if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
            throw new ConflictException("El nombre de usuario ya existe"); 
        }
        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new ConflictException("El email ya está registrado"); 
        }
        String passCifrada = passwordEncoder.encode(nuevoUsuario.getPassword());
        nuevoUsuario.setPassword(passCifrada);
        return usuarioRepository.save(nuevoUsuario);
    }
    
    
    // --- MÉTODOS DE GESTIÓN DE PERFIL (RF04) ---

    public UsuarioResponseDTO getMiPerfil(String username) {
        // ... (código sin cambios)
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el token: " + username));
        return new UsuarioResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO updateMiPerfil(String usernameActual, UsuarioUpdateDTO updateDto) { 
        // ... (código sin cambios)
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Token de usuario inválido.")); 
        String nuevoUsername = updateDto.getUsername().trim();
        if (usuarioActual.getUsername().equals(nuevoUsername)) {
            return new UsuarioResponseDTO(usuarioActual); 
        }
        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(nuevoUsername);
        if (usuarioExistente.isPresent()) {
            throw new ConflictException("El nombre de usuario '" + nuevoUsername + "' ya está en uso. Por favor, elige otro."); 
        }
        usuarioActual.setUsername(nuevoUsername);
        Usuario usuarioActualizado = usuarioRepository.save(usuarioActual);
        return new UsuarioResponseDTO(usuarioActualizado);
    }

    @Transactional
    public void changePassword(String usernameActual, PasswordChangeDTO passwordDto) { 
        // ... (código sin cambios)
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Token de usuario inválido."));
        String contraseñaActualPlana = passwordDto.getContraseñaActual();
        String contraseñaActualHasheada = usuarioActual.getPassword();
        if (!passwordEncoder.matches(contraseñaActualPlana, contraseñaActualHasheada)) {
            throw new ConflictException("La contraseña actual es incorrecta."); 
        }
        String nuevaContraseñaPlana = passwordDto.getNuevaContraseña();
        String nuevaContraseñaHasheada = passwordEncoder.encode(nuevaContraseñaPlana);
        usuarioActual.setPassword(nuevaContraseñaHasheada);
        usuarioRepository.save(usuarioActual);
    }
    
    // --- ¡NUEVO MÉTODO! (Petición 6) ---
    /**
     * Actualiza la URL de la foto de perfil del usuario.
     */
    @Transactional
    public UsuarioResponseDTO updateFotoPerfil(String username, String fotoUrl) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Token de usuario inválido."));
        
        usuario.setFotoPerfilUrl(fotoUrl);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return new UsuarioResponseDTO(usuarioActualizado);
    }
}