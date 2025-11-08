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
 * Servicio para la lógica de negocio relacionada con la entidad Usuario.
 * Implementa la lógica para RF01 (Registro) y RF04 (Gestión de Perfil).
 * REFACTORIZADO: Ahora lanza excepciones de negocio no chequeadas (Runtime).
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Lógica de negocio para registrar un nuevo usuario (RF01).
     * @throws ConflictException si el username o email ya existen.
     */
    public Usuario registrarUsuario(Usuario nuevoUsuario) { 
        
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

    /**
     * Obtiene el perfil del usuario basado en su 'username'.
     */
    public UsuarioResponseDTO getMiPerfil(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el token: " + username));
        
        return new UsuarioResponseDTO(usuario);
    }

    /**
     * Actualiza el 'username' del usuario actual (RF04).
     *
     * @throws ConflictException si el nuevo username ya está en uso (409).
     * @throws ResourceNotFoundException si el usuario del token no existe (404).
     */
    @Transactional
    public UsuarioResponseDTO updateMiPerfil(String usernameActual, UsuarioUpdateDTO updateDto) { // ¡LIMPIO!
        
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

    // --- CAMBIO DE CONTRASEÑA (RF04) ---

    /**
     * Cambia la contraseña del usuario actual tras verificar su identidad.
     *
     * @throws ResourceNotFoundException si el usuario del token no existe (404).
     * @throws ConflictException si la contraseña actual es incorrecta (409).
     */
    @Transactional
    public void changePassword(String usernameActual, PasswordChangeDTO passwordDto) { // ¡LIMPIO!
        
        // 1. Obtener el usuario actual
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new ResourceNotFoundException("Token de usuario inválido."));

        // 2. ¡VERIFICACIÓN DE SEGURIDAD!
        String contraseñaActualPlana = passwordDto.getContraseñaActual();
        String contraseñaActualHasheada = usuarioActual.getPassword();

        if (!passwordEncoder.matches(contraseñaActualPlana, contraseñaActualHasheada)) {
            // Si no coinciden, lanzamos una 409 ya que es un conflicto de credenciales
            throw new ConflictException("La contraseña actual es incorrecta."); 
        }

        // 3. Hasheamos y guardamos la NUEVA contraseña.
        String nuevaContraseñaPlana = passwordDto.getNuevaContraseña();
        String nuevaContraseñaHasheada = passwordEncoder.encode(nuevaContraseñaPlana);

        usuarioActual.setPassword(nuevaContraseñaHasheada);
        usuarioRepository.save(usuarioActual);
    }
}