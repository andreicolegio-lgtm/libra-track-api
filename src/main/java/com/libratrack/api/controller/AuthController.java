// Archivo: src/main/java/com/libratrack/api/controller/AuthController.java
// (¡ACTUALIZADO - SPRINT 10: REFRESH TOKENS!)

package com.libratrack.api.controller;

import com.libratrack.api.dto.LoginResponseDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.entity.RefreshToken; // <-- ¡NUEVA IMPORTACIÓN!
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.TokenRefreshException; // <-- ¡NUEVA IMPORTACIÓN!
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.service.RefreshTokenService; // <-- ¡NUEVA IMPORTACIÓN!
import com.libratrack.api.service.UsuarioService;
import com.libratrack.api.service.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; // <-- ¡NUEVA IMPORTACIÓN!
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap; // <-- ¡NUEVA IMPORTACIÓN!
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * --- ¡ACTUALIZADO (Sprint 10)! ---
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- ¡NUEVA INYECCIÓN DE SERVICIO! ---
    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Endpoint para registrar un nuevo usuario (RF01).
     * (Sin cambios)
     */
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registerUser(@Valid @RequestBody Usuario usuario) {
        UsuarioResponseDTO usuarioRegistrado = usuarioService.registrarUsuario(usuario);
        return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED);
    }

    /**
     * Endpoint para el login de usuario (RF02).
     * --- ¡ACTUALIZADO (ID: QA-074)! ---
     * Ahora genera y devuelve un Access Token y un Refresh Token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.getOrDefault("email", "").trim();
        String password = loginRequest.getOrDefault("password", "").trim();

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("El email y la contraseña no pueden estar vacíos.", HttpStatus.BAD_REQUEST);
        }

        try {
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos."));
            
            String username = usuario.getUsername();
            
            // 1. Autenticar credenciales
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authToken); // Lanza BadCredentialsException si falla
            
            // 2. Generar el Access Token (JWT corto)
            String accessToken = jwtService.generateToken(username);

            // 3. Generar y guardar el Refresh Token (largo, en BD)
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

            // 4. Devolver ambos tokens
            return new ResponseEntity<>(
                new LoginResponseDTO(accessToken, refreshToken.getToken()), 
                HttpStatus.OK
            );

        } catch (IllegalArgumentException e) {
            logger.warn("Login failed (email not found): {}", email);
            return new ResponseEntity<>("Usuario o contraseña incorrectos.", HttpStatus.UNAUTHORIZED);
        
        } catch (BadCredentialsException e) {
            logger.warn("Login failed (bad credentials) for email: {}", email);
            return new ResponseEntity<>("Usuario o contraseña incorrectos.", HttpStatus.UNAUTHORIZED);
            
        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", email, e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * --- ¡NUEVO ENDPOINT (ID: QA-074)! ---
     * Endpoint para refrescar un Access Token caducado.
     * Recibe: { "refreshToken": "..." }
     * Devuelve: { "accessToken": "...", "refreshToken": "..." }
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        if (requestRefreshToken == null || requestRefreshToken.isBlank()) {
            return new ResponseEntity<>("Se requiere un Refresh Token.", HttpStatus.BAD_REQUEST);
        }

        try {
            // 1. Buscar el token en la BD
            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                    .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token no encontrado en la base de datos."));

            // 2. Verificar si ha caducado (lanza excepción si lo está)
            refreshTokenService.verifyExpiration(refreshToken);

            // 3. Si es válido, obtener el usuario
            Usuario usuario = refreshToken.getUsuario();

            // 4. Generar un nuevo Access Token
            String newAccessToken = jwtService.generateToken(usuario.getUsername());

            // 5. Devolver el nuevo Access Token y el Refresh Token original
            return ResponseEntity.ok(new LoginResponseDTO(newAccessToken, requestRefreshToken));
            
        } catch (TokenRefreshException e) {
            // La excepción ya tiene @ResponseStatus(HttpStatus.FORBIDDEN)
            logger.warn("Intento de refresco fallido: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * --- ¡NUEVO ENDPOINT (ID: QA-074)! ---
     * Endpoint para cerrar sesión (invalidar el Refresh Token).
     * Recibe: { "refreshToken": "..." }
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        if (requestRefreshToken == null || requestRefreshToken.isBlank()) {
            return new ResponseEntity<>("Se requiere un Refresh Token.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Elimina el token de la BD (si existe)
            refreshTokenService.deleteByToken(requestRefreshToken);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cierre de sesión exitoso.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return new ResponseEntity<>("Error interno del servidor durante el cierre de sesión.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}