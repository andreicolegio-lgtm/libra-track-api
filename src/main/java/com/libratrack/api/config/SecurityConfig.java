package com.libratrack.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Le dice a Spring que esta clase contiene configuración
@EnableWebSecurity // Activa la seguridad web de Spring
public class SecurityConfig {

    /**
     * Este es el "Bean" (objeto gestionado por Spring) que usaremos para cifrar.
     * BCrypt es el algoritmo estándar de la industria para hashear contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Este es el "Rulebook" (libro de reglas) de nuestra seguridad.
     * Aquí definimos qué rutas son públicas y cuáles están protegidas.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF: Es una protección para webs, no necesaria
            // para una API REST pura que no usa cookies de sesión.
            .csrf(csrf -> csrf.disable())

            // 2. Definir las reglas de autorización (quién puede acceder a qué)
            .authorizeHttpRequests(auth -> auth
                // Permitimos que CUALQUIERA acceda a nuestras rutas de autenticación (RF01, RF02)
                .requestMatchers("/api/auth/**").permitAll()
                // Cualquier otra petición (ej. /api/elementos) requerirá autenticación
                .anyRequest().authenticated()
            )
            
            // 3. Configurar la gestión de sesiones
            // Le decimos a Spring que NO cree sesiones. Una API REST debe ser "stateless"
            // (sin estado). Cada petición se autenticará por sí misma (con un token JWT).
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}