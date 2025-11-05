package com.libratrack.api.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails; // Importa la interfaz de UserDetails
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio de utilidad para manejar JSON Web Tokens (JWT).
 * Sus responsabilidades son:
 * 1. Generar (crear) nuevos tokens cuando un usuario inicia sesión.
 * 2. Validar y extraer información (claims) de los tokens entrantes.
 */
@Service
public class JwtService {

    // --- 1. LA CLAVE SECRETA (FIRMA) ---

    /**
     * Esta es la clave secreta que solo el servidor conoce. Se usa para "firmar"
     * cada token. Cuando un token regresa, usamos esta clave para verificar
     * que la firma es auténtica y que el token no ha sido modificado.
     *
     * IMPORTANTE: En producción real, esta clave NUNCA debe estar en el código.
     * Debería estar en un archivo de configuración externo (como application.properties
     * o una variable de entorno) para mantenerla segura.
     * Para este TFG, está bien tenerla aquí.
     */
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    
    // --- 2. GENERACIÓN DE TOKENS ---

    /**
     * Método público principal para generar un token para un usuario.
     *
     * @param username El nombre de usuario (el "subject" del token).
     * @return Un string con el token JWT firmado.
     */
    public String generateToken(String username) {
        // Los "claims" son piezas de información guardadas en el token (el payload).
        Map<String, Object> claims = new HashMap<>();
        // (Aquí podríamos añadir más claims, como los roles, ej: claims.put("roles", listaDeRoles))
        return createToken(claims, username);
    }

    /**
     * Método privado que construye el token.
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims) // 1. Añade el payload (claims)
                .setSubject(username) // 2. Establece el "dueño" (subject) del token
                .setIssuedAt(new Date(System.currentTimeMillis())) // 3. Establece la fecha de creación (ahora)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 4. Caducidad: 10 horas
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // 5. Firma el token con nuestra clave secreta
                .compact(); // 6. Compila todo en un string seguro
    }

    /**
     * Convierte la clave secreta (String) en un objeto Key que la librería
     * de JWT (jjwt) puede utilizar para firmar.
     * Usa Base64 para decodificar la clave.
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    
    // --- 3. VALIDACIÓN Y LECTURA DE TOKENS ---
    // (Estos métodos son usados por el JwtAuthFilter para verificar el token)

    /**
     * Extrae el nombre de usuario (el "subject") del token.
     *
     * @param token El token JWT.
     * @return El nombre de usuario (username).
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Comprueba si un token es válido (cumple 2 condiciones):
     * 1. El username del token coincide con el username de la base de datos (UserDetails).
     * 2. El token no ha caducado.
     *
     * @param token El token JWT.
     * @param userDetails El objeto UserDetails cargado desde la BD (por UserDetailsServiceImpl).
     * @return true si el token es válido.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Comprueba si el token ha caducado.
     * @param token El token JWT.
     * @return true si la fecha de caducidad es ANTERIOR a la fecha actual.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de caducidad del token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    
    // --- 4. MÉTODOS AUXILIARES (INTERNOS) ---

    /**
     * Método genérico y reutilizable para extraer cualquier "claim" (información)
     * del token de forma segura. Usa una Función de Java para resolver el claim.
     *
     * @param token El token JWT.
     * @param claimsResolver Una función que especifica qué claim extraer (ej. Claims::getSubject).
     * @return El claim específico.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Decodifica el token completo usando la clave secreta.
     * Este es el "parser" (analizador) principal que valida la firma
     * y extrae todo el payload (los "claims").
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey()) // Usa nuestra clave secreta para verificar la firma
                .build()
                .parseClaimsJws(token) // Analiza el token
                .getBody(); // Devuelve el payload
    }
}