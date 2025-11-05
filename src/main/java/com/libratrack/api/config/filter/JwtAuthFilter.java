package com.libratrack.api.config.filter;

import com.libratrack.api.service.UserDetailsServiceImpl;
import com.libratrack.api.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT.
 * Esta clase es el "portero" de la API. Se ejecuta en CADA petición.
 * Intercepta la petición, busca el token JWT en la cabecera 'Authorization',
 * lo valida y, si es válido, establece la autenticación del usuario
 * en el Contexto de Seguridad de Spring.
 */
@Component // Le dice a Spring que esta es una clase gestionada (un "Bean")
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // El servicio que sabe leer tokens

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl; // El servicio que sabe buscar usuarios

    /**
     * Este es el método que intercepta todas las peticiones.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraer la cabecera "Authorization"
        // (La cabecera debe lucir así: "Bearer eyJhbGciOiJIUzI1NiJ9...")
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        // 2. Comprobar si la cabecera es válida y es un token "Bearer"
        // Si no hay cabecera, o no empieza con "Bearer ", ignoramos el filtro
        // y continuamos (Spring Security lo bloqueará después por ser 'anyRequest().authenticated()')
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continúa al siguiente filtro
            return; // Termina la ejecución de este filtro
        }

        // 3. Extraer el token (quitando el prefijo "Bearer ")
        token = authHeader.substring(7);
        
        try {
            // 4. Extraer el 'username' del token usando el JwtService
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Si el token está caducado o la firma es incorrecta,
            // el 'username' será nulo y la validación fallará.
            // (Opcional: registrar el error 'e.getMessage()')
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Si tenemos un 'username' y el usuario AÚN NO está autenticado
        // en el contexto de seguridad actual...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 6. ...cargamos los detalles del usuario desde la BD
            // (Llamamos a nuestro UserDetailsServiceImpl)
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

            // 7. Validamos el token (comprobamos que no ha caducado y
            // que el 'username' del token coincide con el de la BD)
            if (jwtService.validateToken(token, userDetails)) {
                
                // 8. Si el token es válido, creamos una "autenticación"
                // para Spring Security con los datos y roles del usuario.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // El "Principal" (quién es)
                        null, // No pasamos credenciales (contraseña), ya está validado
                        userDetails.getAuthorities() // Los roles (ej. ROLE_USER)
                );
                
                // 9. Añadimos detalles de la petición (ej. IP) a la autenticación
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 10. ¡ÉXITO! Guardamos la autenticación en el Contexto de Seguridad
                // Spring Security ahora "sabe" que este usuario está logueado
                // para esta petición.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 11. Continuamos con el resto de filtros (ej. el filtro de Autorización)
        filterChain.doFilter(request, response);
    }
}