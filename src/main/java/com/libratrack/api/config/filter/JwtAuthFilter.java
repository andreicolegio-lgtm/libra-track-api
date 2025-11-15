// Archivo: src/main/java/com/libratrack/api/config/filter/JwtAuthFilter.java
// (¡CORREGIDO!)

package com.libratrack.api.config.filter;

import com.libratrack.api.service.UserDetailsServiceImpl;
import com.libratrack.api.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// --- ¡NUEVA IMPORTACIÓN! ---
import io.jsonwebtoken.ExpiredJwtException;
// ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtService jwtService; 

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl; 

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("Authorization header is missing or does not start with 'Bearer '");
            filterChain.doFilter(request, response); 
            return; 
        }

        token = authHeader.substring(7);

        try {
            username = jwtService.extractUsername(token);

        // --- ¡BLOQUE 'CATCH' MODIFICADO (ID: QA-067)! ---
        } catch (ExpiredJwtException e) {
            // ¡ESTA ES LA CORRECCIÓN!
            // Si el token está caducado, detenemos el filtro aquí mismo
            // y enviamos el 401 que el frontend espera.
            logger.warn("JWT Token has expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token JWT caducado");
            return; // No continuamos con filterChain.doFilter()
            
        } catch (Exception e) {
            // Para otros errores (ej. firma inválida)
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token JWT inválido");
            return; // No continuamos
        }
        // ---

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities() 
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Authentication successful for user: {}", username);
            } else {
                logger.warn("Token validation failed for user: {}", username);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}