// Archivo: src/main/java/com/libratrack/api/service/UserDetailsServiceImpl.java
package com.libratrack.api.service;

import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * --- ¡ACTUALIZADO (Sprint 4)! ---
 * --- ¡ACTUALIZADO (ID: QA-010)! Refactorizado para usar lógica centralizada de roles ---
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // 2. Definimos los "roles" (autoridades)
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // Rol base para todos
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // --- ¡LÓGICA DE ROLES REFACTORIZADA! (ID: QA-010) ---
        // Se usan los métodos helper de la entidad Usuario
        
        // 2a. Comprobamos si es Administrador
        if (usuario.esAdmin()) { // <-- REFACTORIZADO
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            // (Petición 16) Un Admin es implícitamente un Moderador
            authorities.add(new SimpleGrantedAuthority("ROLE_MODERADOR")); 
        } 
        // 2b. Si no es Admin, comprobamos si es Moderador
        else if (usuario.esMod()) { // <-- REFACTORIZADO
            authorities.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
        }
        
        // 3. Devolvemos el objeto 'User' de Spring
        return new User(
            usuario.getUsername(),
            usuario.getPassword(),
            authorities // La lista de roles actualizada
        );
    }
}