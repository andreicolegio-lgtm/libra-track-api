package com.libratrack.api.controller;

import com.libratrack.api.entity.Usuario; // Tu entidad Usuario
import com.libratrack.api.service.UsuarioService; // Tu servicio de Usuario
import jakarta.validation.Valid; // Para validar los objetos Usuario en la petición
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Para los códigos de estado HTTP
import org.springframework.http.ResponseEntity; // Para construir respuestas HTTP
import org.springframework.validation.BindingResult; // Para manejar errores de validación
import org.springframework.web.bind.annotation.*; // Para las anotaciones REST

import java.util.HashMap;
import java.util.Map;

@RestController // Indica a Spring que esta clase es un controlador REST
@RequestMapping("/api/auth") // Todas las rutas de este controlador empezarán con /api/auth
public class AuthController {

    @Autowired // Inyecta tu servicio de usuario aquí
    private UsuarioService usuarioService;

    /**
     * Endpoint para registrar un nuevo usuario (RF01).
     *
     * URL: POST /api/auth/register
     * Cuerpo de la petición: { "username": "...", "email": "...", "password": "..." }
     *
     * @param usuario El objeto Usuario enviado en el cuerpo de la petición.
     * @param bindingResult Objeto para capturar errores de validación.
     * @return ResponseEntity con el usuario registrado o errores.
     */
    @PostMapping("/register") // Este método responderá a peticiones POST en /api/auth/register
    public ResponseEntity<?> registerUser(@Valid @RequestBody Usuario usuario, BindingResult bindingResult) {
        // 1. Manejar errores de validación (por ejemplo, si el email no es válido)
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Código 400
        }

        try {
            // 2. Llamar al servicio para registrar al usuario
            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
            // Quitamos la contraseña de la respuesta por seguridad (aunque luego la cifremos)
            usuarioRegistrado.setPassword(null); 
            return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED); // Código 201 (Creado)
        } catch (Exception e) {
            // 3. Manejar errores del servicio (ej. email o username ya existen)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Código 409 (Conflicto)
        }
    }

    /**
     * Endpoint para el login de usuario (RF02).
     *
     * URL: POST /api/auth/login
     * Cuerpo de la petición: { "username": "...", "password": "..." }
     *
     * @param loginRequest Un mapa con el username y el password.
     * @return ResponseEntity con el usuario logueado o errores.
     */
    @PostMapping("/login") // Este método responderá a peticiones POST en /api/auth/login
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // Validaciones básicas antes de llamar al servicio
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new ResponseEntity<>("El nombre de usuario y la contraseña no pueden estar vacíos", HttpStatus.BAD_REQUEST);
        }

        try {
            // 1. Llamar al servicio para intentar el login
            Usuario usuarioLogueado = usuarioService.login(username, password);
            // Quitamos la contraseña de la respuesta por seguridad
            usuarioLogueado.setPassword(null);
            return new ResponseEntity<>(usuarioLogueado, HttpStatus.OK); // Código 200 (OK)
        } catch (Exception e) {
            // 2. Manejar errores del servicio (ej. credenciales incorrectas)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED); // Código 401 (No autorizado)
        }
    }
}