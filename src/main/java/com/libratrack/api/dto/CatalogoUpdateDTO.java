package com.libratrack.api.dto;

import com.libratrack.api.model.EstadoPersonal; // Importa el Enum de estado
import jakarta.validation.constraints.Size; // Import para validación de tamaño

/**
 * DTO (Data Transfer Object) para la petición de ACTUALIZAR
 * una entrada en el catálogo personal.
 *
 * Este "molde" define el JSON que la app móvil debe enviar
 * (vía un 'PUT') para cumplir con los requisitos RF06 y RF07.
 *
 * Nota: Los campos aquí son 'Optional' (no marcados con @NotNull)
 * porque el usuario puede querer actualizar solo el estado,
 * solo el progreso, o ambos a la vez. El servicio manejará esta lógica.
 */
public class CatalogoUpdateDTO {

    /**
     * El nuevo estado que el usuario quiere asignar (RF06).
     * (Ej: PENDIENTE, EN_PROGRESO, TERMINADO, ABANDONADO)
     */
    private EstadoPersonal estadoPersonal;

    /**
     * El nuevo texto de progreso que el usuario quiere registrar (RF07).
     * (Ej: "T4:E3", "Cap. 7")
     */
    @Size(max = 100, message = "El progreso no puede exceder los 100 caracteres")
    private String progresoEspecifico;

    
    // --- Getters y Setters ---
    // Necesarios para que Spring/Jackson pueda mapear el JSON
    // entrante a este objeto.

    public EstadoPersonal getEstadoPersonal() {
        return estadoPersonal;
    }

    public void setEstadoPersonal(EstadoPersonal estadoPersonal) {
        this.estadoPersonal = estadoPersonal;
    }

    public String getProgresoEspecifico() {
        return progresoEspecifico;
    }

    public void setProgresoEspecifico(String progresoEspecifico) {
        this.progresoEspecifico = progresoEspecifico;
    }
}