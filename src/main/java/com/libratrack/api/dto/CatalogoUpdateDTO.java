package com.libratrack.api.dto;

import com.libratrack.api.model.EstadoPersonal;
import jakarta.validation.constraints.Min;

/**
 * DTO para la petición de ACTUALIZAR una entrada en el catálogo personal (RF06, RF07).
 * (Progreso detallado)
 */
public class CatalogoUpdateDTO {

    private EstadoPersonal estadoPersonal;

    /**
     * Nueva temporada actual del progreso (para Series/Anime).
     */
    @Min(value = 1, message = "La temporada mínima es 1")
    private Integer temporadaActual; 

    /**
     * Nuevo episodio/capítulo/página actual del progreso.
     */
    @Min(value = 0, message = "La unidad mínima es 0 (antes de empezar)")
    private Integer unidadActual;
    
    // --- Getters y Setters ---

    public EstadoPersonal getEstadoPersonal() {
        return estadoPersonal;
    }

    public void setEstadoPersonal(EstadoPersonal estadoPersonal) {
        this.estadoPersonal = estadoPersonal;
    }

    public Integer getTemporadaActual() {
        return temporadaActual;
    }

    public void setTemporadaActual(Integer temporadaActual) {
        this.temporadaActual = temporadaActual;
    }

    public Integer getUnidadActual() {
        return unidadActual;
    }

    public void setUnidadActual(Integer unidadActual) {
        this.unidadActual = unidadActual;
    }
}