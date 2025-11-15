// Archivo: src/main/java/com/libratrack/api/service/UsuarioService.java
package com.libratrack.api.service;

import com.libratrack.api.dto.PasswordChangeDTO;
import com.libratrack.api.dto.RolUpdateDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.dto.UsuarioUpdateDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ConflictException;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.repository.UsuarioRepository;

// --- ¡NUEVAS IMPORTACIONES! ---
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
// ---

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList; 
import java.util.List;
import java.util.Optional;

// --- ¡NUEVAS IMPORTACIONES PARA GOOGLE (ID: QA-091)! ---
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import java.util.UUID;
// ---

/**
 * --- ¡ACTUALIZADO (Sprint 7)! ---
 * --- ¡ACTUALIZADO (Sprint 10 / ID: QA-091)! ---
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ... (registrarUsuario, getMiPerfil, updateMiPerfil, changePassword, updateFotoPerfil ... sin cambios) ...
    @Transactional
    public UsuarioResponseDTO registrarUsuario(Usuario nuevoUsuario) {
        if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
            throw new ConflictException("El nombre de usuario ya existe.");
        }
        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new ConflictException("El email ya está registrado.");
        }
        String passCifrada = passwordEncoder.encode(nuevoUsuario.getPassword());
        nuevoUsuario.setPassword(passCifrada);
        nuevoUsuario.setEsModerador(false);
        nuevoUsuario.setEsAdministrador(false);
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        return new UsuarioResponseDTO(usuarioGuardado);
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponseDTO getMiPerfil(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el token: " + username));
        return new UsuarioResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO updateMiPerfil(String usernameActual, UsuarioUpdateDTO updateDto) {
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
    
    @Transactional
    public UsuarioResponseDTO updateFotoPerfil(String username, String fotoUrl) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Token de usuario inválido."));
        usuario.setFotoPerfilUrl(fotoUrl);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuarioActualizado);
    }
    
    // --- MÉTODOS DE GESTIÓN DE ADMIN (Petición 14) ---
    
    /**
     * --- ¡REFACTORIZADO (Sprint 7)! ---
     * (Petición B, C, G) Obtiene la lista de todos los usuarios
     * con paginación, búsqueda y filtrado de roles.
     * Solo para Admins.
     */
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> getAllUsuarios(Pageable pageable, String search, String roleFilter) {
        
        // 1. Creamos la "Especificación" (la consulta dinámica)
        Specification<Usuario> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 2. (Petición C) Filtro de Búsqueda (busca en username Y email)
            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                Predicate searchUsername = cb.like(cb.lower(root.get("username")), likePattern);
                Predicate searchEmail = cb.like(cb.lower(root.get("email")), likePattern);
                predicates.add(cb.or(searchUsername, searchEmail));
            }

            // 3. (Petición G) Filtro de Roles
            if (roleFilter != null && !roleFilter.isBlank()) {
                if ("MODERADOR".equalsIgnoreCase(roleFilter)) {
                    predicates.add(cb.isTrue(root.get("esModerador")));
                } else if ("ADMIN".equalsIgnoreCase(roleFilter)) {
                    predicates.add(cb.isTrue(root.get("esAdministrador")));
                }
            }

            // Combinamos todos los filtros con AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 4. Ejecutamos la consulta paginada con los filtros
        Page<Usuario> usuarios = usuarioRepository.findAll(spec, pageable);
        
        // 5. Convertimos la página de Entidades a DTOs
        return usuarios.map(UsuarioResponseDTO::new);
    }

    /**
     * (Petición 14 - PUT) Actualiza los roles de un usuario.
     */
    @Transactional
    public UsuarioResponseDTO updateUserRoles(Long usuarioId, RolUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario con ID: " + usuarioId));
        usuario.setEsModerador(dto.getEsModerador());
        usuario.setEsAdministrador(dto.getEsAdministrador());
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuarioActualizado);
    }

    // --- ¡NUEVO MÉTODO (ID: QA-091)! ---
    /**
     * Busca un usuario por su email (de Google).
     * Si no existe, lo crea.
     * @param payload El payload del token de Google verificado.
     * @return El usuario (existente o nuevo) de nuestra BD.
     */
    @Transactional
    public Usuario findOrCreateGoogleUser(Payload payload) {
        String email = payload.getEmail();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            // --- Escenario 1: El usuario ya existe ---
            Usuario usuario = usuarioOpt.get();
            
            // (Opcional) Actualizamos su foto de perfil si la de Google es más nueva
            if (payload.get("picture") != null) {
                usuario.setFotoPerfilUrl((String) payload.get("picture"));
            }
            return usuarioRepository.save(usuario);

        } else {
            // --- Escenario 2: Es un nuevo usuario ---
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(email);

            // Generamos un username único (ej. "andrei1234")
            String username = (String) payload.get("given_name");
            if (username == null || username.isBlank()) {
                username = email.split("@")[0]; // "andrei@gmail.com" -> "andrei"
            }
            // Asegura que el username base no sea muy largo
            if (username.length() > 40) {
                username = username.substring(0, 40);
            }
            nuevoUsuario.setUsername(generarUsernameUnico(username));

            // Creamos una contraseña aleatoria y segura que nunca se usará
            String randomPassword = UUID.randomUUID().toString();
            nuevoUsuario.setPassword(passwordEncoder.encode(randomPassword));
            
            if (payload.get("picture") != null) {
                nuevoUsuario.setFotoPerfilUrl((String) payload.get("picture"));
            }
            
            // Todos los usuarios de Google empiezan como USER
            nuevoUsuario.setEsModerador(false);
            nuevoUsuario.setEsAdministrador(false);

            return usuarioRepository.save(nuevoUsuario);
        }
    }
    
    /**
     * Helper para asegurar que el username es único.
     * Si "andrei" existe, prueba "andrei1", "andrei2", etc.
     */
    private String generarUsernameUnico(String baseUsername) {
        String username = baseUsername;
        int i = 1;
        // Bucle para encontrar un username único, evitando que sea > 50 caracteres
        while (usuarioRepository.existsByUsername(username)) {
            String suffix = String.valueOf(i);
            if (baseUsername.length() + suffix.length() > 50) {
                // Si "username_base" + "numero" es muy largo, acortamos la base
                username = baseUsername.substring(0, 50 - suffix.length()) + suffix;
            } else {
                username = baseUsername + suffix;
            }
            i++;
        }
        return username;
    }
}