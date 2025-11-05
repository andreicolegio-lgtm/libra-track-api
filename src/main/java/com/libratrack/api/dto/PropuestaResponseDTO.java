package com.libratrack.api.dto;

import com.libratrack.api.entity.PropuestaElemento; // Importa la entidad
import com.libratrack.api.model.EstadoPropuesta; // Importa el Enum
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la RESPUESTA al ENVIAR una Propuesta.
 *
 * Esta clase es crucial para la estabilidad de la API.
 * Su propósito es "aplanar" la entidad 'PropuestaElemento', convirtiendo
 * las relaciones complejas (objetos 'Usuario' para proponente y revisor)
 * en datos simples (sus 'username' como Strings).
 *
 * Esto soluciona la 'LazyInitializationException' (Error 500/403)
 * que ocurre al intentar devolver la entidad 'PropuestaElemento' (con
 * relaciones LAZY) directamente desde el ModeracionController.
 */
public class PropuestaResponseDTO {

    // --- Campos de Datos Primitivos ---
    private Long id;
    private String tituloSugerido;
    private String descripcionSugerida;
    private String tipoSugerido;
    private String generosSugeridos;
    private EstadoPropuesta estadoPropuesta;
    private String comentariosRevision;
    private LocalDateTime fechaPropuesta;

    // --- Relaciones Aplanadas ---
    
    /**
     * El nombre de usuario del 'proponente'.
     * (Aplanado desde la entidad 'Usuario' relacionada).
     */
    private String proponenteUsername;
    
    /**
     * El nombre de usuario del 'revisor' (Moderador).
     * Este campo será 'null' si la propuesta aún está PENDIENTE.
     */
    private String revisorUsername;

    
    // --- Constructor de Mapeo ---

    /**
     * Constructor especial que sabe cómo "mapear" (convertir)
     * una entidad 'PropuestaElemento' (de la BD) a este DTO (para la respuesta JSON).
     *
     * @param p La entidad 'PropuestaElemento' leída de la base de datos.
     */
    public PropuestaResponseDTO(PropuestaElemento p) {
        // Mapeo 1 a 1 de campos simples
        this.id = p.getId();
        this.tituloSugerido = p.getTituloSugerido();
        this.descripcionSugerida = p.getDescripcionSugerida();
        this.tipoSugerido = p.getTipoSugerido();
        this.generosSugeridos = p.getGenerosSugeridos();
        this.estadoPropuesta = p.getEstadoPropuesta();
        this.comentariosRevision = p.getComentariosRevision();
        this.fechaPropuesta = p.getFechaPropuesta();

        // Mapeo Seguro (Aplanado) de Relaciones LAZY
        
        // 1. Aplanar Proponente (siempre existe)
        this.proponenteUsername = p.getProponente().getUsername();
        
        // 2. Aplanar Revisor (con comprobación de nulo)
        // El revisor es nulo hasta que la propuesta se aprueba o rechaza.
        if (p.getRevisor() != null) {
            this.revisorUsername = p.getRevisor().getUsername();
        } else {
            this.revisorUsername = null; // Explícitamente nulo
        }
    }

    
    // --- Getters ---
    // Necesarios para que Spring/Jackson pueda leer los valores
    // y construir el JSON de respuesta.

    public Long getId() { return id; }
    public String getTituloSugerido() { return tituloSugerido; }
    public String getDescripcionSugerida() { return descripcionSugerida; }
    public String getTipoSugerido() { return tipoSugerido; }
    public String getGenerosSugeridos() { return generosSugeridos; }
    public EstadoPropuesta getEstadoPropuesta() { return estadoPropuesta; }
    public String getComentariosRevision() { return comentariosRevision; }
    public LocalDateTime getFechaPropuesta() { return fechaPropuesta; }
    public String getProponenteUsername() { return proponenteUsername; }
    public String getRevisorUsername() { return revisorUsername; }
}