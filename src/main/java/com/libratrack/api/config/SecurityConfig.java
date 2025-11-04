package com.libratrack.api.config;

import com.libratrack.api.config.filter.JwtAuthFilter;
import com.libratrack.api.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    /**
     * El "Cifrador" de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * El "Verificador": le dice a Spring cómo buscar usuarios
     * (usa nuestro UserDetailsServiceImpl) y qué cifrador usamos.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * El "Jefe de Seguridad": Gestiona la autenticación.
     * AuthController lo usará para procesar el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * El "Libro de Reglas" de la API (el Filtro)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs REST
            
            // Define qué rutas son públicas y cuáles privadas
            .authorizeHttpRequests(auth -> auth
                // 1. Rutas Públicas (Registro y Login)
                .requestMatchers("/api/auth/**").permitAll()
                
                // 2. Rutas de Moderación (RF14, RF15)
                // SOLO usuarios con el rol "ROLE_MODERADOR" pueden acceder
                .requestMatchers("/api/moderacion/**").hasAuthority("ROLE_MODERADOR")
                
                // 3. Rutas de Usuario (Proponer, Catálogo, Reseñas, etc.)
                // (Usamos HttpMethod para ser específicos)
                .requestMatchers(HttpMethod.POST, "/api/propuestas").hasAuthority("ROLE_USER")
                .requestMatchers("/api/catalogo/**").hasAuthority("ROLE_USER")
                .requestMatchers("/api/resenas/**").hasAuthority("ROLE_USER")
                .requestMatchers("/api/elementos/**").hasAuthority("ROLE_USER")
                
                // 4. CUALQUIER OTRA petición (que no hayamos listado) será denegada
                .anyRequest().authenticated()
            )
            
            // Le dice a Spring que NO cree sesiones (API "stateless")
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Le dice a Spring que use nuestro "Verificador"
            .authenticationProvider(authenticationProvider())
            
            // Le dice a Spring que use nuestro "Portero" (JwtAuthFilter)
            // ANTES de su filtro de login estándar.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}