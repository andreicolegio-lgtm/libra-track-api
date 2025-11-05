package com.libratrack.api.controller;

import com.libratrack.api.dto.ElementoResponseDTO; // DTO para enviar el Elemento (evita error 500)
import com.libratrack.api.dto.PropuestaResponseDTO; // DTO para enviar la Propuesta (evita error 500)
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.repository.UsuarioRepository; // Para buscar al revisor
import com.libratrack.api.service.PropuestaElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ¡Import para la seguridad a nivel de método!
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Import para obtener el usuario del token
import java.util.List;

/**
 * Controlador REST para las rutas de Moderación (el "Panel de Administración").
 * Implementa los endpoints para RF14 y RF15.
 *
 * Seguridad:
 * Esta es la implementación del RF03 (Gestión de Roles).
 * La anotación @EnableMethodSecurity en SecurityConfig nos permite usar
 * @PreAuthorize aquí. El acceso a este controlador está restringido
 * solo a usuarios que posean la autoridad 'ROLE_MODERADOR'.
 */
@RestController
@RequestMapping("/api/moderacion") // Ruta base para todas las acciones de moderación
public class ModeracionController {

    @Autowired
    private PropuestaElementoService propuestaService; // El "cerebro" de la lógica

    @Autowired
    private UsuarioRepository usuarioRepo; // Para buscar la entidad del moderador

    /**
     * Endpoint para obtener la cola de propuestas pendientes (RF14).
     * Escucha en: GET /api/moderacion/pendientes
     *
     * Seguridad:
     * Solo los usuarios autenticados Y que tengan la autoridad 'ROLE_MODERADOR'
     * (según UserDetailsServiceImpl) pueden acceder a esta ruta.
     *
     * @return ResponseEntity con una Lista de DTOs de las propuestas pendientes (200 OK).
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAuthority('ROLE_MODERADOR')") // ¡Seguridad a nivel de método!
    public ResponseEntity<List<PropuestaResponseDTO>> getPropuestasPendientes() {
        List<PropuestaResponseDTO> propuestas = propuestaService.getPropuestasPendientes();
        return ResponseEntity.ok(propuestas);
    }

    /**
     * Endpoint para aprobar una propuesta (RF15).
     * Escucha en: POST /api/moderacion/aprobar/1 (donde 1 es el ID de la propuesta)
     *
     * Seguridad:
     * Protegido con @PreAuthorize.
     * Obtiene la identidad del moderador (Revisor) desde el token (Principal)
     * para asegurar la trazabilidad (sabemos quién aprobó qué).
     *
     * @param propuestaId El ID de la PropuestaElemento a aprobar (de la URL).
     * @param principal El objeto de seguridad que contiene el 'username' del moderador.
     * @return ResponseEntity:
     * - 200 (OK) con el DTO del *nuevo Elemento* creado (ElementoResponseDTO).
     * - 400 (Bad Request) si la propuesta no existe, faltan datos, etc.
     */
    @PostMapping("/aprobar/{propuestaId}")
    @PreAuthorize("hasAuthority('ROLE_MODERADOR')") // ¡Seguridad a nivel de método!
    public ResponseEntity<?> aprobarPropuesta(@PathVariable Long propuestaId, Principal principal) {

        try {
            // 1. Obtenemos el nombre del moderador desde el token
            String revisorUsername = principal.getName();
            
            // 2. Buscamos la entidad Usuario del moderador
            Usuario revisor = usuarioRepo.findByUsername(revisorUsername)
                    .orElseThrow(() -> new Exception("Token de revisor inválido. Usuario no encontrado."));

            // 3. ¡Usamos el ID real del moderador!
            Long revisorId = revisor.getId(); 

            // 4. Llamamos al servicio de "traducción" y aprobación
            ElementoResponseDTO nuevoElemento = propuestaService.aprobarPropuesta(propuestaId, revisorId);
            
            // 5. Devolvemos el nuevo elemento creado
            return ResponseEntity.ok(nuevoElemento);
            
        } catch (Exception e) {
            // 6. Manejo de errores (ej. "Propuesta no encontrada")
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // (En una V2, añadiríamos un endpoint @PostMapping("/rechazar/{propuestaId}")
    // que también usaría @PreAuthorize("hasAuthority('ROLE_MODERADOR')")
    // y llamaría a un servicio 'rechazarPropuesta(propuestaId, revisorId, motivo)')
}