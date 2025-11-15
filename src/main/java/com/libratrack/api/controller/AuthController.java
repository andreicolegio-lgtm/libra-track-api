// Archivo: src/main/java/com/libratrack/api/controller/AuthController.java
// (¡ACTUALIZADO - SPRINT 10: GOOGLE SIGN-IN!)

package com.libratrack.api.controller;

// --- ¡NUEVAS IMPORTACIONES PARA GOOGLE (ID: QA-091)! ---
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.libratrack.api.dto.GoogleTokenDTO;
// ---

import com.libratrack.api.dto.LoginResponseDTO;
import com.libratrack.api.dto.UsuarioResponseDTO;
import com.libratrack.api.entity.RefreshToken;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.exception.TokenRefreshException;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.service.RefreshTokenService;
import com.libratrack.api.service.UsuarioService;
import com.libratrack.api.service.jwt.JwtService;
import jakarta.validation.Valid;

// --- ¡NUEVAS IMPORTACIONES (ID: QA-091)! ---
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
// ---

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * --- ¡ACTUALIZADO (Sprint 10)! ---
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // --- ¡NUEVA INYECCIÓN DE PROPIEDAD (ID: QA-091)! ---
    @Value("${libratrack.app.googleWebClientId}")
    private String googleWebClientId;
    // ---

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    
    // --- ¡NUEVO ENDPOINT (ID: QA-091)! ---
    /**
     * Endpoint para el login/registro con Google (Sprint 10).
     * Recibe un token de ID de Google, lo verifica, y si es válido,
     * crea o actualiza al usuario y devuelve nuestros propios tokens (Access y Refresh).
     */
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@Valid @RequestBody GoogleTokenDTO googleTokenDTO) {
        
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(googleWebClientId))
            .build();

        try {
            // 1. Verificar el token de Google
            GoogleIdToken idToken = verifier.verify(googleTokenDTO.getToken());
            if (idToken == null) {
                logger.warn("Intento de login con token de Google inválido.");
                return new ResponseEntity<>("Token de Google inválido.", HttpStatus.UNAUTHORIZED);
            }

            // 2. Extraer el payload y buscar o crear al usuario
            GoogleIdToken.Payload payload = idToken.getPayload();
            Usuario usuario = usuarioService.findOrCreateGoogleUser(payload);

            // 3. Generar nuestros propios tokens (igual que en el login normal)
            String accessToken = jwtService.generateToken(usuario.getUsername());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getUsername());

            logger.info("Login (Google) exitoso para: {}", usuario.getEmail());
            
            // 4. Devolver nuestros tokens
            return new ResponseEntity<>(
                new LoginResponseDTO(accessToken, refreshToken.getToken()), 
                HttpStatus.OK
            );

        } catch (GeneralSecurityException | IOException e) {
            logger.error("Error al verificar el token de Google: {}", e.getMessage());
            return new ResponseEntity<>("Error al verificar el token de Google.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error inesperado en login con Google: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error interno del servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // ---


    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registerUser(@Valid @RequestBody Usuario usuario) {
        UsuarioResponseDTO usuarioRegistrado = usuarioService.registrarUsuario(usuario);
        return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED);
    }

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
            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authToken); 
            
            String accessToken = jwtService.generateToken(username);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        if (requestRefreshToken == null || requestRefreshToken.isBlank()) {
            return new ResponseEntity<>("Se requiere un Refresh Token.", HttpStatus.BAD_REQUEST);
        }

        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                    .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token no encontrado en la base de datos."));

            refreshTokenService.verifyExpiration(refreshToken);
            Usuario usuario = refreshToken.getUsuario();
            String newAccessToken = jwtService.generateToken(usuario.getUsername());

            return ResponseEntity.ok(new LoginResponseDTO(newAccessToken, requestRefreshToken));
            
        } catch (TokenRefreshException e) {
            logger.warn("Intento de refresco fallido: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        if (requestRefreshToken == null || requestRefreshToken.isBlank()) {
            return new ResponseEntity<>("Se requiere un Refresh Token.", HttpStatus.BAD_REQUEST);
        }

        try {
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