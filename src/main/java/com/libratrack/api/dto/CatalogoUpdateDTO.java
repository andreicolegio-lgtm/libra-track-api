package com.libratrack.api.dto;

import com.libratrack.api.model.EstadoPersonal;

/**
 * DTO para actualizar una entrada en el catálogo personal.
 * El usuario enviará un JSON con los campos que quiere cambiar (RF06, RF07).
 */
public class CatalogoUpdateDTO {

    private EstadoPersonal estadoPersonal;
    private String progresoEspecifico;

    // --- Getters y Setters ---

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