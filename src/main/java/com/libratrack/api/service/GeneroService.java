package com.libratrack.api.service;

import com.libratrack.api.entity.Genero; // Importa la entidad Genero
import com.libratrack.api.repository.GeneroRepository; // Importa el repositorio Genero
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la lógica de negocio relacionada con la entidad Genero.
 * Gestiona la creación y recuperación de Géneros de contenido (ej. "Ciencia Ficción").
 */
@Service // Le dice a Spring que esta clase es un "Bean" de Servicio
public class GeneroService {

    @Autowired // Inyecta la instancia del repositorio
    private GeneroRepository generoRepository;

    /**
     * Devuelve una lista de todos los Géneros disponibles en la base de datos.
     * Esto es útil para el frontend, para rellenar menús desplegables
     * o filtros de búsqueda.
     *
     * @return Lista de todas las entidades Genero.
     */
    public List<Genero> getAllGeneros() {
        return generoRepository.findAll();
    }

    /**
     * Crea y guarda un nuevo Genero en la base de datos.
     * Esta es una acción administrativa, llamada (por ahora) por el GeneroController.
     * También es llamada internamente por el PropuestaElementoService si
     * un moderador aprueba un género que no existe (RF15).
     *
     * @param genero El objeto Genero a guardar (generalmente solo con el nombre).
     * @return La entidad Genero tal como fue guardada (con su nuevo ID).
     * @throws Exception Si ocurre un error (ej. el nombre ya existe, violando
     * la restricción 'unique' de la BD).
     */
    public Genero createGenero(Genero genero) throws Exception {
        // (En una V2, podríamos añadir validación aquí para comprobar si
        // genero.getNombre() ya existe, antes de llamar a save())
        return generoRepository.save(genero);
    }
}