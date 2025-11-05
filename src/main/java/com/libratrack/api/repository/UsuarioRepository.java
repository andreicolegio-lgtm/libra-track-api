package com.libratrack.api.repository;

import com.libratrack.api.entity.Usuario; // Importa la entidad Usuario
import org.springframework.data.jpa.repository.JpaRepository; // Importa la funcionalidad principal de JPA
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importa Optional, una forma segura de manejar valores nulos

/**
 * Repositorio para la entidad Usuario.
 * Extiende JpaRepository, lo que nos da gratis métodos CRUD (Create, Read, Update, Delete)
 * como save(), findById(), findAll(), deleteById(), etc.
 */
@Repository // Le dice a Spring que esta es una interfaz de acceso a datos
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // JpaRepository<TipoDeLaEntidad, TipoDelID>

    // --- Métodos Mágicos (Query Methods) ---
    // Spring Data JPA crea automáticamente la consulta SQL basándose
    // en el nombre del método.

    /**
     * Busca un usuario por su 'username'.
     * Spring traduce esto a: "SELECT * FROM usuarios WHERE username = ?"
     * Esencial para UserDetailsServiceImpl (seguridad JWT).
     *
     * @param username El nombre de usuario a buscar.
     * @return Un 'Optional' que contendrá al Usuario si se encuentra.
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por su 'email'.
     * Spring traduce esto a: "SELECT * FROM usuarios WHERE email = ?"
     * Esencial para el AuthController (login con email, RF02).
     *
     * @param email El email a buscar.
     * @return Un 'Optional' que contendrá al Usuario si se encuentra.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Comprueba eficientemente si un 'username' ya existe.
     * Spring traduce esto a: "SELECT COUNT(*) > 0 FROM usuarios WHERE username = ?"
     * Usado por el UsuarioService para validar el registro (RF01).
     *
     * @param username El nombre de usuario a comprobar.
     * @return true si ya existe, false si no.
     */
    Boolean existsByUsername(String username);

    /**
     * Comprueba eficientemente si un 'email' ya existe.
     * Usado por el UsuarioService para validar el registro (RF01).
     *
     * @param email El email a comprobar.
     * @return true si ya existe, false si no.
     */
    Boolean existsByEmail(String email);

}