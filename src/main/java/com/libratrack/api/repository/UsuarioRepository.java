package com.libratrack.api.repository;

import com.libratrack.api.entity.Usuario; // Importa tu clase Entidad Usuario
import org.springframework.data.jpa.repository.JpaRepository; // Importa la magia de Spring Data JPA
import org.springframework.stereotype.Repository;

import java.util.Optional; // Un tipo de dato para manejar valores que pueden ser nulos

@Repository // Le dice a Spring que esto es un Repositorio (un "Bean" de acceso a datos)
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // JpaRepository<Entidad, TipoDelID>
    // Al extender JpaRepository, automáticamente tenemos métodos como:
    // - save(usuario) -> Guardar o actualizar un usuario
    // - findById(id) -> Buscar un usuario por su ID
    // - findAll() -> Buscar todos los usuarios
    // - deleteById(id) -> Borrar un usuario

    // --- Métodos Mágicos de Spring Data JPA ---
    // Spring es tan inteligente que si defines un método con un nombre
    // específico, él sabe qué consulta SQL crear.

    /**
     * Busca un usuario por su nombre de usuario (username).
     * Esto nos servirá para el login (RF02).
     *
     * @param username El nombre de usuario a buscar.
     * @return Un 'Optional' que contendrá al Usuario si lo encuentra.
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por su email.
     * Esto nos servirá para comprobar si un email ya está registrado (RF01).
     *
     * @param email El email a buscar.
     * @return Un 'Optional' que contendrá al Usuario si lo encuentra.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Comprueba si existe un usuario con ese nombre de usuario.
     *
     * @param username El nombre de usuario a comprobar.
     * @return true si existe, false si no.
     */
    Boolean existsByUsername(String username);

    /**
     * Comprueba si existe un usuario con ese email.
     *
     * @param email El email a comprobar.
     * @return true si existe, false si no.
     */
    Boolean existsByEmail(String email);

}