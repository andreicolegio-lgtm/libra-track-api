package com.libratrack.api.service;

import com.libratrack.api.dto.PasswordChangeDTO; // --- NUEVA IMPORTACIÓN ---
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.dto.UsuarioUpdateDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Importa el cifrador
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * Servicio para la lógica de negocio relacionada con la entidad Usuario.
 * Implementa la lógica para RF01 (Registro) y RF04 (Gestión de Perfil).
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Lógica de negocio para registrar un nuevo usuario (RF01).
     * [Código original 100% preservado]
     */
    public Usuario registrarUsuario(Usuario nuevoUsuario) throws Exception {
        
        if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
            throw new Exception("El nombre de usuario ya existe");
        }

        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new Exception("El email ya está registrado");
        }

        String passCifrada = passwordEncoder.encode(nuevoUsuario.getPassword());
        nuevoUsuario.setPassword(passCifrada);
        return usuarioRepository.save(nuevoUsuario);
    }
    
    
    // --- MÉTODOS DE GESTIÓN DE PERFIL (RF04) ---

    /**
     * Obtiene el perfil del usuario basado en su 'username'.
     * [Código 100% preservado]
     */
    public UsuarioResponseDTO getMiPerfil(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el token: " + username));
        
        return new UsuarioResponseDTO(usuario);
    }

    /**
     * Actualiza el 'username' del usuario actual (RF04).
     * [Código 100% preservado]
     */
    @Transactional
    public UsuarioResponseDTO updateMiPerfil(String usernameActual, UsuarioUpdateDTO updateDto) throws Exception {
        
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new RuntimeException("Token de usuario inválido."));

        String nuevoUsername = updateDto.getUsername().trim();

        if (usuarioActual.getUsername().equals(nuevoUsername)) {
            return new UsuarioResponseDTO(usuarioActual); 
        }

        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(nuevoUsername);
        
        if (usuarioExistente.isPresent()) {
            throw new Exception("El nombre de usuario '" + nuevoUsername + "' ya está en uso. Por favor, elige otro.");
        }

        usuarioActual.setUsername(nuevoUsername);
        Usuario usuarioActualizado = usuarioRepository.save(usuarioActual);

        return new UsuarioResponseDTO(usuarioActualizado);
    }

    // --- NUEVO MÉTODO AÑADIDO (RF04 - Cambio de Contraseña) ---

    /**
     * Cambia la contraseña del usuario actual tras verificar su identidad.
     *
     * @param usernameActual El 'username' del token JWT.
     * @param passwordDto DTO que contiene la contraseña actual y la nueva.
     * @throws Exception Si la contraseña actual es incorrecta.
     */
    @Transactional
    public void changePassword(String usernameActual, PasswordChangeDTO passwordDto) throws Exception {
        
        // 1. Obtener el usuario actual
        Usuario usuarioActual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new RuntimeException("Token de usuario inválido."));

        // 2. ¡VERIFICACIÓN DE SEGURIDAD!
        // Comparamos la contraseña actual (texto plano) que envía el usuario
        // con la contraseña hasheada que tenemos en la BD.
        String contraseñaActualPlana = passwordDto.getContraseñaActual();
        String contraseñaActualHasheada = usuarioActual.getPassword(); // De la BD

        if (!passwordEncoder.matches(contraseñaActualPlana, contraseñaActualHasheada)) {
            // Si no coinciden, es un intento no autorizado.
            throw new Exception("La contraseña actual es incorrecta.");
        }

        // 3. (Éxito) El usuario está verificado. Hasheamos y guardamos la NUEVA contraseña.
        String nuevaContraseñaPlana = passwordDto.getNuevaContraseña();
        String nuevaContraseñaHasheada = passwordEncoder.encode(nuevaContraseñaPlana);

        usuarioActual.setPassword(nuevaContraseñaHasheada);
        usuarioRepository.save(usuarioActual);
        
        // No es necesario devolver nada, un 200 OK es suficiente.
    }
}