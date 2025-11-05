package com.libratrack.api.model;

/**
 * Define los estados de seguimiento que un usuario puede asignar a un
 * elemento dentro de su catálogo personal.
 * Esto es la base del requisito funcional RF06.
 */
public enum EstadoPersonal {
    /**
     * El usuario ha añadido el elemento a su lista, pero aún no lo ha empezado.
     * (Ej: "Libros por leer").
     */
    PENDIENTE,

    /**
     * El usuario está consumiendo activamente el contenido.
     * (Ej: "Viendo la Temporada 2").
     */
    EN_PROGRESO,

    /**
     * El usuario ha completado el contenido.
     */
    TERMINADO,

    /**
     * El usuario empezó el contenido pero decidió no continuar.
     */
    ABANDONADO
}