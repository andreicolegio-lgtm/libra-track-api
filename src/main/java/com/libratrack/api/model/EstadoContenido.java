package com.libratrack.api.model;

/**
 * Define los dos estados de "confianza" para una entidad Elemento.
 * Esto es crucial para el requisito RF16, permitiendo a los administradores
 * diferenciar el contenido validado (Oficial) del contenido aprobado
 * por moderadores (Comunitario). 
 */
public enum EstadoContenido {
    /**
     * El contenido ha sido añadido o verificado por el Administrador (dueño de la app).
     * Es la fuente de mayor confianza.
     */
    OFICIAL,

    /**
     * El contenido fue sugerido por un usuario (RF13) y aprobado por un
     * Moderador (RF15). Es válido, pero se marca como de origen comunitario. 
     */
    COMUNITARIO
}