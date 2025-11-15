// Archivo: src/main/java/com/libratrack/api/service/jwt/JwtService.java
// (¡ACTUALIZADO!)

package com.libratrack.api.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// --- ¡NUEVA IMPORTACIÓN! ---
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio de utilidad para manejar JSON Web Tokens (JWT).
 * --- ¡ACTUALIZADO (ID: QA-072)! ---
 * - La duración del Access Token (JWT) ahora se inyecta desde 
 * application.properties (libratrack.app.jwtAccessExpirationMs)
 * - La duración se ha reducido a 30 minutos.
 */
@Service
public class JwtService {

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // --- ¡NUEVA INYECCIÓN DE PROPIEDAD! ---
    @Value("${libratrack.app.jwtAccessExpirationMs}")
    private Long jwtAccessExpirationMs;
    
    // --- 2. GENERACIÓN DE TOKENS ---

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Método privado que construye el token.
     * --- ¡ACTUALIZADO (ID: QA-072)! ---
     * Se usa la nueva duración de 30 minutos.
     */
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                // ¡LÍNEA MODIFICADA! Se usa la propiedad inyectada
                .expiration(new Date(System.currentTimeMillis() + jwtAccessExpirationMs))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    
    // --- 3. VALIDACIÓN Y LECTURA DE TOKENS ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    
    // --- 4. MÉTODOS AUXILIARES (INTERNOS) ---

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}