package com.libratrack.api.model;

/**
 * Enum que define el estado de publicación o disponibilidad del contenido (Punto 11).
 * (Ej: EN_EMISION, FINALIZADO, DISPONIBLE, CANCELADO)
 */
public enum EstadoPublicacion {
    EN_EMISION,     // Series, Anime, Manga publicándose activamente
    FINALIZADO,     // Series, Anime, Libros, etc., cuya publicación ha terminado
    DISPONIBLE,     // Videojuegos, contenido que está simplemente "disponible"
    CANCELADO       // Contenido que ha sido cancelado
}