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
 * Implementación personalizada del UserDetailsService de Spring Security.
 * Esta clase es el "puente" entre la entidad 'Usuario' de nuestra API
 * y el objeto 'UserDetails' que Spring Security entiende.
 * * Su único trabajo es implementar el método 'loadUserByUsername'.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método principal que Spring Security (específicamente el JwtAuthFilter y
     * el AuthenticationManager) llama para cargar los datos de un usuario.
     *
     * NOTA: A pesar del nombre del método, nuestro 'AuthController' está
     * configurado para que el 'username' que llega aquí sea el 'username'
     * (ej. "testuser"), NO el email.
     *
     * @param username El identificador del usuario (el 'username', que es el
     * "subject" de nuestro token JWT).
     * @return Un objeto UserDetails que Spring Security utilizará para
     * gestionar la autenticación y autorización.
     * @throws UsernameNotFoundException Si el usuario no se encuentra en la BD.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // 1. Buscamos al usuario en nuestro repositorio usando el username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // 2. Definimos los "roles" (autoridades) de este usuario (RF03)
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // 2a. Asignamos el rol básico que todos los usuarios registrados tienen
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // 2b. Comprobamos el flag de moderador (RF03, RF14)
        // Si el campo 'esModerador' de nuestra entidad Usuario es 'true',
        // añadimos la autoridad 'ROLE_MODERADOR' a la lista.
        // Spring Security usará esta lista para las reglas @PreAuthorize.
        if (usuario.getEsModerador() != null && usuario.getEsModerador()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MODERADOR"));
        }
        
        // (En el futuro, podríamos añadir "ROLE_ADMINISTRADOR" aquí)

        // 3. Devolvemos el objeto 'User' (de Spring)
        // Spring usará esta información internamente para:
        // - Comprobar la contraseña (usando el hash de la BD)
        // - Almacenar los roles (authorities) en el contexto de seguridad
        return new User(
            usuario.getUsername(),
            usuario.getPassword(), // La contraseña ya cifrada de la BD
            authorities // La lista de roles
        );
    }
}