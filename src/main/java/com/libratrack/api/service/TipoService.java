package com.libratrack.api.service;

import com.libratrack.api.entity.Tipo;
import com.libratrack.api.repository.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoService {

    @Autowired
    private TipoRepository tipoRepository;

    /**
     * Devuelve una lista de todos los Tipos disponibles
     * (Ej: ["Serie", "Libro", "Película"])
     * @return Lista de entidades Tipo.
     */
    public List<Tipo> getAllTipos() {
        return tipoRepository.findAll();
    }

    /**
     * Crea un nuevo Tipo (Solo para Admins).
     * @param tipo El objeto Tipo a guardar.
     * @return El Tipo guardado.
     */
    public Tipo createTipo(Tipo tipo) throws Exception {
        // (Podríamos añadir validación para que no se repitan nombres)
        return tipoRepository.save(tipo);
    }
}