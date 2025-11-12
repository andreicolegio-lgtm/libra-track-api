package com.libratrack.api.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    // El objeto 'Storage' es inyectado automáticamente
    // por la dependencia de Spring Cloud GCP.
    @Autowired
    private Storage storage;

    // Inyectamos el nombre del bucket desde application.properties
    @Value("${gcs.bucket.name}")
    private String bucketName;

    /**
     * Sube un archivo a Google Cloud Storage y devuelve su URL pública.
     *
     * @param file El archivo MultipartFile (imagen) enviado en la petición.
     * @return La URL pública completa (https://...) de la imagen subida.
     */
    public String storeFile(MultipartFile file) {
        
        // 1. Validar archivo
        if (file.isEmpty()) {
            throw new RuntimeException("Error: El archivo está vacío.");
        }
        
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";

        try {
            if (originalFileName.contains("..")) {
                throw new RuntimeException("El nombre del archivo contiene una secuencia de ruta inválida.");
            }
            
            // 2. Generar un nombre de archivo único
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFileName.substring(dotIndex);
            }
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // 3. Crear el "BlobId" (la "dirección" del archivo en GCS)
            BlobId blobId = BlobId.of(bucketName, uniqueFileName);
            
            // 4. Configurar la información del Blob (tipo de contenido)
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                                    .setContentType(file.getContentType())
                                    .build();

            // 5. Subir el archivo
            storage.create(blobInfo, file.getBytes());

            // 6. Devolver la URL pública
            // (Esto funciona porque hicimos el bucket público para 'allUsers'
            // con el rol 'Visualizador de objetos de Storage')
            return "https://storage.googleapis.com/" + bucketName + "/" + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo. Error: " + e.getMessage());
        }
    }
}