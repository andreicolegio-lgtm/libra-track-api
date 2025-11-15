// Archivo: src/main/java/com/libratrack/api/service/RefreshTokenService.java
// (¡ACTUALIZADO!)

package com.libratrack.api.service;

import com.libratrack.api.entity.RefreshToken;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.ResourceNotFoundException;
import com.libratrack.api.exception.TokenRefreshException;
import com.libratrack.api.repository.RefreshTokenRepository;
import com.libratrack.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// --- ¡NUEVAS IMPORTACIONES! ---
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// ---

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestionar la lógica de los Tokens de Refresco (Paso 3).
 * --- ¡ACTUALIZADO (ID: QA-082)! ---
 */
@Service
public class RefreshTokenService {

    // --- ¡NUEVO LOGGER! ---
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${libratrack.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca un token de refresco en la BD por su valor.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Crea un nuevo token de refresco para un usuario.
     */
    @Transactional
    public RefreshToken createRefreshToken(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        RefreshToken refreshToken = new RefreshToken();
        
        refreshToken.setUsuario(usuario);
        refreshToken.setFechaExpiracion(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString()); // Genera un token aleatorio seguro

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    /**
     * Verifica si un token de refresco ha caducado.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getFechaExpiracion().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token); // Lo eliminamos de la BD
            throw new TokenRefreshException(token.getToken(), "Token de refresco caducado. Por favor, inicie sesión de nuevo.");
        }
        return token;
    }

    /**
     * Elimina un token de refresco de la BD usando su valor (UUID).
     */
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
            .ifPresent(refreshTokenRepository::delete);
    }

    
    // --- ¡NUEVO MÉTODO (ID: QA-082)! ---
    /**
     * Tarea de Limpieza Programada (Cron Job).
     * Se ejecuta automáticamente todos los días a las 4:00 AM.
     * (CRON: "segundo minuto hora día-del-mes mes día-de-la-semana")
     *
     * Busca y elimina de la base de datos todos los tokens de refresco
     * que ya hayan caducado (tokens "muertos").
     */
    @Transactional
    @Scheduled(cron = "0 0 4 * * ?") // 4:00 AM todos los días
    public void purgeExpiredTokens() {
        Instant now = Instant.now();
        logger.info("Ejecutando tarea de limpieza de tokens de refresco caducados (anteriores a {})...", now);
        
        // Llama al nuevo método del repositorio
        refreshTokenRepository.deleteByFechaExpiracionBefore(now);
        
        logger.info("Tarea de limpieza de tokens completada.");
    }
}