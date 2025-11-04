package com.libratrack.api.dto;

import com.libratrack.api.entity.CatalogoPersonal;
import com.libratrack.api.model.EstadoPersonal;
import java.time.LocalDateTime;

/**
 * DTO para ENVIAR una entrada del catálogo al cliente (móvil).
 * Resuelve la LazyInitializationException.
 */
public class CatalogoPersonalResponseDTO {

    private Long id;
    private EstadoPersonal estadoPersonal;
    private String progresoEspecifico;
    private LocalDateTime agregadoEn;
    
    // Datos "planos" de las relaciones
    private Long elementoId;
    private String elementoTitulo;
    private Long usuarioId;

    // --- Constructor ---
    // Un constructor que sabe cómo "mapear" la Entidad al DTO
    public CatalogoPersonalResponseDTO(CatalogoPersonal entrada) {
        this.id = entrada.getId();
        this.estadoPersonal = entrada.getEstadoPersonal();
        this.progresoEspecifico = entrada.getProgresoEspecifico();
        this.agregadoEn = entrada.getAgregadoEn();
        this.elementoId = entrada.getElemento().getId();
        this.elementoTitulo = entrada.getElemento().getTitulo(); // Damos también el título
        this.usuarioId = entrada.getUsuario().getId();
    }

    // --- Getters ---
    // (Spring los necesita para el JSON)

    public Long getId() { return id; }
    public EstadoPersonal getEstadoPersonal() { return estadoPersonal; }
    public String getProgresoEspecifico() { return progresoEspecifico; }
    public LocalDateTime getAgregadoEn() { return agregadoEn; }
    public Long getElementoId() { return elementoId; }
    public String getElementoTitulo() { return elementoTitulo; }
    public Long getUsuarioId() { return usuarioId; }
}