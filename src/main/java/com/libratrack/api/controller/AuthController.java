package com.libratrack.api.controller;

import com.libratrack.api.dto.LoginResponseDTO; // DTO para enviar el token
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository; // Importa el repositorio de usuario
import com.libratrack.api.service.UsuarioService; // Importa el servicio de usuario
import com.libratrack.api.service.jwt.JwtService; // Importa el servicio de JWT
import jakarta.validation.Valid; // Para activar la validación en el DTO/Entidad
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Para códigos de estado HTTP (ej. 201, 409)
import org.springframework.http.ResponseEntity; // Para construir la respuesta HTTP
import org.springframework.security.authentication.AuthenticationManager; // El "Jefe de Seguridad"
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult; // Para capturar errores de validación
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para las rutas de autenticación públicas (Registro y Login).
 * Esta es la única ruta que, según SecurityConfig, es accesible sin un token.
 * Implementa los endpoints para RF01 y RF02.
 */
@RestController // Indica a Spring que esta clase es un Controlador y devuelve JSON
@RequestMapping("/api/auth") // Todas las rutas de esta clase empezarán con /api/auth
public class AuthController {

    // --- Inyección de Dependencias ---
    // (Spring nos proporciona las instancias)

    @Autowired
    private UsuarioService usuarioService; // El "cerebro" para la lógica de registro

    @Autowired
    private JwtService jwtService; // El "cerebro" para crear tokens

    @Autowired
    private AuthenticationManager authenticationManager; // El "Jefe de Seguridad" para el login

    @Autowired
    private UsuarioRepository usuarioRepository; // Para la lógica de "traducción" email->username

    /**
     * Endpoint para registrar un nuevo usuario (RF01).
     * Escucha en: POST /api/auth/register
     *
     * @param usuario El objeto Usuario (con username, email, password) que viene en el
     * cuerpo (Body) JSON de la petición.
     * @param bindingResult Objeto de Spring que captura automáticamente los errores
     * de validación (ej. @NotBlank, @Email) de la entidad Usuario.
     * @return ResponseEntity:
     * - 201 (Created) con el usuario si el registro es exitoso.
     * - 400 (Bad Request) si la validación falla (ej. email inválido).
     * - 409 (Conflict) si el email o username ya existen.
     */
    @PostMapping("/register") // Mapea este método a peticiones POST en /api/auth/register
    public ResponseEntity<?> registerUser(@Valid @RequestBody Usuario usuario, BindingResult bindingResult) {
        
        // 1. Manejar errores de validación (de @NotBlank, @Size, @Email en la Entidad)
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            // Recorre los errores y los pone en un mapa para enviarlos como JSON
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // Devuelve 400
        }

        try {
            // 2. Llamar al servicio (el "cerebro") para registrar al usuario
            Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuario);
            
            // 3. (Buena Práctica de Seguridad)
            // Nunca devolvemos la contraseña en la respuesta, ni siquiera cifrada.
            usuarioRegistrado.setPassword(null); 
            
            return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED); // Devuelve 201

        } catch (Exception e) {
            // 4. Capturar errores del servicio (ej. "El email ya existe")
            // (Lanzados desde UsuarioService)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Devuelve 409
        }
    }

    /**
     * Endpoint para el login de usuario (RF02), usando EMAIL y CONTRASEÑA.
     * Escucha en: POST /api/auth/login
     *
     * @param loginRequest Un Mapa que recoge el JSON del body (ej. {"email": "...", "password": "..."})
     * @return ResponseEntity:
     * - 200 (OK) con el Token JWT si el login es exitoso.
     * - 400 (Bad Request) si faltan datos.
     * - 401 (Unauthorized) si las credenciales son incorrectas.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new ResponseEntity<>("El email y la contraseña no pueden estar vacíos", HttpStatus.BAD_REQUEST);
        }

        try {
            // --- LÓGICA DE LOGIN PROFESIONAL (CON TRADUCCIÓN) ---
            
            // 1. "Traducir" Email a Username:
            // Buscamos al usuario por su email para obtener su 'username',
            // ya que nuestro sistema de seguridad (UserDetailsServiceImpl)
            // funciona con 'username' como identificador principal.
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new Exception("Usuario o contraseña incorrectos"));

            String username = usuario.getUsername();

            // 2. Autenticar:
            // Creamos un "ticket" de autenticación con el USERNAME y la contraseña.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

            // 3. Entregamos el ticket al "Jefe de Seguridad" (AuthenticationManager).
            // Él se encarga (internamente) de llamar a UserDetailsServiceImpl
            // y al PasswordEncoder para verificar que todo sea correcto.
            // Si la contraseña es incorrecta, lanzará una excepción.
            authenticationManager.authenticate(authToken);

            // 4. Generar Token:
            // Si la línea anterior no falló, el usuario es válido.
            // Generamos un token JWT usando su 'username'.
            String token = jwtService.generateToken(username);

            // 5. Devolver el Token
            return new ResponseEntity<>(new LoginResponseDTO(token), HttpStatus.OK); // Devuelve 200

        } catch (Exception e) {
            // Si 'authenticate' falla (BadCredentialsException) o
            // 'findByEmail' falla (UsernameNotFoundException),
            // devolvemos un error 401 genérico por seguridad.
            return new ResponseEntity<>("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED); // Devuelve 401
        }
    }
}