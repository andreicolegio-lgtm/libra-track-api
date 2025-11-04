package com.libratrack.api.service;

import com.libratrack.api.entity.Genero;
import com.libratrack.api.repository.GeneroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneroService {

    @Autowired
    private GeneroRepository generoRepository;

    /**
     * Devuelve una lista de todos los Géneros disponibles
     * (Ej: ["Ciencia Ficción", "Drama"])
     * @return Lista de entidades Genero.
     */
    public List<Genero> getAllGeneros() {
        return generoRepository.findAll();
    }

    /**
     * Crea un nuevo Genero (Solo para Admins).
     * @param genero El objeto Genero a guardar.
     * @return El Genero guardado.
     */
    public Genero createGenero(Genero genero) throws Exception {
        return generoRepository.save(genero);
    }
}