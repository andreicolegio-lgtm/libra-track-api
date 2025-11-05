package com.libratrack.api.config;

import com.libratrack.api.config.filter.JwtAuthFilter;
import com.libratrack.api.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // ¡IMPORTANTE! Activa @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// --- Imports para CORS ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import static org.springframework.security.config.Customizer.withDefaults; // Import para .cors(withDefaults())

/**
 * Configuración central de Spring Security.
 * Define la cadena de filtros, el proveedor de autenticación,
 * el cifrador de contraseñas y las reglas de autorización.
 */
@Configuration // Le dice a Spring que esta clase contiene Beans de configuración
@EnableWebSecurity // Activa la seguridad web de Spring
@EnableMethodSecurity // Activa la seguridad a nivel de método (ej. @PreAuthorize)
public class SecurityConfig {

    // --- Inyección de Dependencias ---
    // Inyectamos nuestro filtro JWT y nuestro servicio de detalles de usuario
    
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    /**
     * Define el "Cifrador" de contraseñas.
     * Usamos BCrypt, el algoritmo estándar de la industria.
     * Spring usará este Bean automáticamente para comprobar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el "Verificador" (AuthenticationProvider).
     * Le dice a Spring Security cómo debe buscar a los usuarios.
     * 1. Le asigna nuestro UserDetailsServiceImpl (para buscar en la BD).
     * 2. Le asigna nuestro PasswordEncoder (para comprobar la contraseña).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Usamos la forma moderna de construir el proveedor, pasando
        // el servicio en el constructor para evitar métodos obsoletos.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Define el "Jefe de Seguridad" (AuthenticationManager).
     * Este es el Bean que el AuthController usa explícitamente para
     * procesar la petición de login del usuario.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing).
     * Esencial para permitir que nuestra app Flutter (que se ejecuta
     * en un "origen" diferente, el emulador) pueda hacer peticiones
     * a nuestra API (que se ejecuta en 'localhost:8080').
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite peticiones de cualquier origen (inseguro para producción,
        // pero necesario para el desarrollo con emuladores).
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        // Permite los métodos HTTP estándar que usará nuestra API
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite las cabeceras necesarias (Authorization para el token JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración a TODAS las rutas de la API ("/**")
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    /**
     * El "Libro de Reglas" principal de la API (la Cadena de Filtros).
     * Aquí se define el orden de los filtros y las reglas de acceso básicas.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF: Es una protección para navegadores
            // (basada en cookies) que no es necesaria para una API REST "stateless".
            .csrf(csrf -> csrf.disable())
            
            // 2. Activar CORS: Usa la configuración definida en el Bean 'corsConfigurationSource'
            .cors(withDefaults())
            
            // 3. Definir las reglas de autorización (el "libro de reglas" simplificado)
            .authorizeHttpRequests(auth -> auth
                // 3a. Rutas Públicas: Solo el registro y el login son públicos
                .requestMatchers("/api/auth/**").permitAll()
                
                // 3b. CUALQUIER OTRA petición (ej. /api/elementos, /api/catalogo)
                // debe estar autenticada (requiere un token JWT válido).
                .anyRequest().authenticated()
            )
            // (La autorización específica por ROL, como 'hasRole("MODERADOR")',
            // la manejamos directamente en los Controladores con @PreAuthorize)
            
            // 4. Gestión de Sesión:
            // Le decimos a Spring que NO cree sesiones de usuario.
            // Nuestra API es "stateless" (sin estado). Cada petición
            // se valida a sí misma con el token JWT.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 5. Proveedor de Autenticación:
            // Le dice a Spring que use nuestro "Verificador" (authenticationProvider)
            // para gestionar la autenticación.
            .authenticationProvider(authenticationProvider())
            
            // 6. El "Portero" (Filtro JWT):
            // Le dice a Spring que use nuestro "Portero" (JwtAuthFilter)
            // ANTES de su filtro de login estándar (UsernamePasswordAuthenticationFilter).
            // Esto asegura que interceptemos y validemos el token en cada petición.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}