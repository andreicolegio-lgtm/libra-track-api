package com.libratrack.api.controller;

import com.libratrack.api.dto.LoginResponseDTO;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository;
import com.libratrack.api.service.UsuarioService;
import com.libratrack.api.service.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador REST para las rutas de autenticación públicas (Registro y Login).
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

    /**
     * Endpoint para registrar un nuevo usuario (RF01).
     *
     * REFACTORIZADO: Se eliminó el BindingResult y el try-catch.
     * La validación (@Valid) se captura en GlobalExceptionHandler.
     * Los errores de negocio (ConflictException) se capturan en GlobalExceptionHandler.
     */
    @PostMapping("/register") 
    public ResponseEntity<Usuario> registerUser(@Valid @RequestBody Usuario usuario) { // Se eliminó BindingResult
        
        // 1. Llama al servicio (el servicio lanzará ConflictException si falla)
        Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
        
        // 2. (Buena Práctica de Seguridad)
        usuarioRegistrado.setPassword(null); 
        
        // 3. Devuelve 201 Created
        return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED); 
    }

    /**
     * Endpoint para el login de usuario (RF02), usando EMAIL y CONTRASEÑA.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.getOrDefault("email", "").trim();
        String password = loginRequest.getOrDefault("password", "").trim();

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("El email y la contraseña no pueden estar vacíos", HttpStatus.BAD_REQUEST);
        }

        try {
            // 1. "Traducir" Email a Username:
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrectos"));

            String username = usuario.getUsername();

            // 2. Autenticar:
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

            // 3. Entregamos el ticket al "Jefe de Seguridad" (si falla, lanza excepción)
            authenticationManager.authenticate(authToken);

            // 4. Generar Token:
            String token = jwtService.generateToken(username);

            // 5. Devolver el Token
            return new ResponseEntity<>(new LoginResponseDTO(token), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.warn("Login failed for email: {}", email);
            return new ResponseEntity<>("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}