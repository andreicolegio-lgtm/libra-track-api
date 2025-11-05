package com.libratrack.api.model;

/**
 * Define el ciclo de vida de una PropuestaElemento en la cola de moderación.
 * Esto es la base del requisito funcional RF15.
 */
public enum EstadoPropuesta {
    /**
     * El estado por defecto. La propuesta ha sido enviada por un usuario (RF13) 
     * y está esperando la revisión de un Moderador (RF14).
     */
    PENDIENTE,

    /**
     * Un Moderador ha revisado y aprobado la propuesta (RF15).
     * El contenido ha sido copiado a la tabla principal 'elementos'.
     */
    APROBADO,

    /**
     * Un Moderador ha revisado y rechazado la propuesta.
     * El contenido NO se moverá a la tabla 'elementos'.
     */
    RECHAZADO
}