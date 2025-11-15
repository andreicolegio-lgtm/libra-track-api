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
import java.util.ArrayList; // <-- ¡NUEVA IMPORTACIÓN!
import java.util.List; 
import java.util.Optional;

/**
 * --- ¡ACTUALIZADO (Sprint 7)! ---
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
        // ... (código sin cambios)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario con ID: " + usuarioId));
        usuario.setEsModerador(dto.getEsModerador());
        usuario.setEsAdministrador(dto.getEsAdministrador());
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(usuarioActualizado);
    }
}