// Archivo: src/main/java/com/libratrack/api/repository/RefreshTokenRepository.java
// (¡CORREGIDO!)

package com.libratrack.api.repository;

import com.libratrack.api.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // <-- ¡IMPORTACIÓN AÑADIDA!

import java.util.Optional;
import java.time.Instant;

/**
 * Repositorio para la entidad RefreshToken (Paso 2 del plan profesional).
 * --- ¡ACTUALIZADO (ID: QA-082)! ---
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Busca un RefreshToken por su valor de token (el UUID).
     */
    Optional<RefreshToken> findByToken(String token);

    // --- ¡NUEVO MÉTODO (ID: QA-082)! ---
    /**
     * Borra de la BD todos los tokens cuya fecha de expiración sea anterior
     * al momento (Instant) proporcionado.
     * Spring Data JPA genera: "DELETE FROM refresh_tokens WHERE fecha_expiracion < ?"
     *
     * @param now El 'Instant' (momento actual) para comparar.
     */
    @Transactional // Esta anotación ahora es válida
    void deleteByFechaExpiracionBefore(Instant now);
}