package com.libratrack.api.controller;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.PropuestaElemento;
import com.libratrack.api.service.PropuestaElementoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/moderacion")
public class ModeracionController {

    @Autowired
    private PropuestaElementoService propuestaService;

    /**
     * Endpoint para obtener la cola de propuestas pendientes (RF14).
     * (Solo accesible para Moderadores/Admins).
     * URL: GET /api/moderacion/pendientes
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<PropuestaElemento>> getPropuestasPendientes() {
        List<PropuestaElemento> propuestas = propuestaService.getPropuestasPendientes();
        return ResponseEntity.ok(propuestas);
    }

    /**
     * Endpoint para aprobar una propuesta (RF15).
     * (Solo accesible para Moderadores/Admins).
     * URL: POST /api/moderacion/aprobar/1 (donde 1 es el ID de la propuesta)
     */
    @PostMapping("/aprobar/{propuestaId}")
    public ResponseEntity<?> aprobarPropuesta(@PathVariable Long propuestaId, Principal principal) {
        // Obtenemos el ID del moderador desde el token
        String revisorUsername = principal.getName();
        
        // TO DO: Necesitamos buscar el ID del usuario a partir del username.
        // Por ahora, simulamos con el ID 1.
        Long revisorId = 1L; // <-- ESTO ES TEMPORAL

        try {
            Elemento nuevoElemento = propuestaService.aprobarPropuesta(propuestaId, revisorId);
            return ResponseEntity.ok(nuevoElemento);
        } catch (Exception e) {
            // (Manejo de errores... por ahora incompleto)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // (Aquí iría el endpoint para rechazar propuestas)
}