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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; 
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
import static org.springframework.security.config.Customizer.withDefaults; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuración central de Spring Security.
 * Define la cadena de filtros, el proveedor de autenticación,
 * el cifrador de contraseñas y las reglas de autorización.
 */
@Configuration 
@EnableWebSecurity 
@EnableMethodSecurity 
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    // --- Inyección de Dependencias ---
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    /**
     * Define el "Cifrador" de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el "Verificador" (AuthenticationProvider).
     * --- ¡ACTUALIZADO! (Spring Boot 3.x / Security 6.x) ---
     * El constructor DaoAuthenticationProvider(UserDetailsService) fue eliminado.
     * Ahora usamos el constructor vacío y el método setUserDetailsService.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // 1. Usamos el constructor vacío
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // 2. Inyectamos nuestro servicio de usuario con el "setter"
        authProvider.setUserDetailsService(userDetailsServiceImpl); 
        
        // 3. Inyectamos el codificador de contraseña
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    /**
     * Define el "Jefe de Seguridad" (AuthenticationManager).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing).
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    /**
     * El "Libro de Reglas" principal de la API (la Cadena de Filtros).
     * (¡Actualizado para incluir /images/** como ruta pública!)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            
            // 3. Definir las reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // 3a. Rutas públicas de Autenticación
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()

                // 3b. Hacemos pública la carpeta de imágenes
                .requestMatchers("/images/**").permitAll() 

                // 3c. CUALQUIER OTRA petición SÍ requiere autenticación
                .anyRequest().authenticated()
            )
            
            // 4. Gestión de Sesión:
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 5. Proveedor de Autenticación:
            .authenticationProvider(authenticationProvider())
            
            // 6. El "Portero" (Filtro JWT):
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("SecurityConfig loaded successfully. Public routes: /api/auth/**, /images/**");
        return http.build();
    }
}