package com.libratrack.api.service;

import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Importa el cifrador
import org.springframework.stereotype.Service;

/**
 * Servicio para la lógica de negocio relacionada con la entidad Usuario.
 * Implementa la lógica para el requisito RF01 (Registro).
 */
@Service // Le dice a Spring que esta clase es un "Bean" de Servicio
public class UsuarioService {

    /**
     * Inyección de Dependencias:
     * Spring nos "inyecta" automáticamente las instancias de estos
     * componentes (que definimos como @Repository y @Bean)
     * para que podamos usarlos.
     */

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // El cifrador (BCrypt) que definimos en SecurityConfig

    /**
     * Lógica de negocio para registrar un nuevo usuario (RF01).
     * Este método es llamado por el AuthController.
     *
     * @param nuevoUsuario El objeto Usuario que llega desde la petición (con la contraseña en texto plano).
     * @return El objeto Usuario tal como se guardó en la BD (con la contraseña cifrada).
     * @throws Exception Si el nombre de usuario o el email ya existen.
     */
    public Usuario registrarUsuario(Usuario nuevoUsuario) throws Exception {
        
        // 1. Validación: Comprobar si el nombre de usuario ya existe
        // (Usamos el método "mágico" del repositorio)
        if (usuarioRepository.existsByUsername(nuevoUsuario.getUsername())) {
            // Lanza una excepción que el controlador capturará y enviará
            // como un error 409 (Conflicto).
            throw new Exception("El nombre de usuario ya existe");
        }

        // 2. Validación: Comprobar si el email ya existe
        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new Exception("El email ya está registrado");
        }

        // 3. ¡Paso de Seguridad Crítico: Cifrado de Contraseña!
        // Nunca, NUNCA, guardamos una contraseña en texto plano.
        // Usamos el 'passwordEncoder' (BCrypt) para crear un "hash"
        // irreversible de la contraseña.
        String passCifrada = passwordEncoder.encode(nuevoUsuario.getPassword());
        
        // 4. Sobrescribimos la contraseña en texto plano con la cifrada
        nuevoUsuario.setPassword(passCifrada);

        // 5. Guardamos el usuario en la base de datos
        // El repositorio se encarga de ejecutar el INSERT SQL.
        return usuarioRepository.save(nuevoUsuario);
    }

    // Nota: El método login() fue eliminado de este servicio
    // porque la autenticación (comprobación de contraseña) ahora
    // la gestiona Spring Security (AuthenticationManager)
    // de forma centralizada y más segura, llamando a UserDetailsServiceImpl.
}