package com.libratrack.api.controller;

import com.libratrack.api.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Endpoint para subir un archivo (imagen de perfil o portada).
     * Solo los usuarios autenticados pueden subir.
     *
     * @param file El archivo enviado como 'multipart/form-data'.
     * @return Un JSON con la URL pública de GCS (ej. {"url": "https://storage..."})
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        
        try {
            // 1. Guardamos el archivo en GCS y obtenemos su URL pública
            String fileUrl = fileStorageService.storeFile(file);

            // 2. Devolvemos el JSON que Flutter espera
            return ResponseEntity.ok(Map.of("url", fileUrl));
            
        } catch (Exception e) {
            // Si algo falla (ej. archivo vacío), devolvemos un error
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error al subir el archivo: " + e.getMessage()));
        }
    }
}