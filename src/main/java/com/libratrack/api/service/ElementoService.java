package com.libratrack.api.service;

import com.libratrack.api.entity.Elemento;
import com.libratrack.api.repository.ElementoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElementoService {

    @Autowired
    private ElementoRepository elementoRepository;

    // (Aquí inyectaremos los repos de Tipo, Genero y Usuario cuando los necesitemos)

    /**
     * Busca todos los elementos en la base de datos (RF09 - Búsqueda Global).
     * (Más adelante haremos que esta búsqueda sea más inteligente, con filtros).
     * @return Una lista de todos los Elementos.
     */
    public List<Elemento> findAllElementos() {
        return elementoRepository.findAll();
    }

    /**
     * Busca un elemento por su ID (RF10 - Ficha Detallada).
     * @param id El ID del elemento a buscar.
     * @return Un Optional que contendrá el Elemento si existe.
     */
    public Optional<Elemento> findElementoById(Long id) {
        return elementoRepository.findById(id);
    }
    
    // --- Próximamente ---
    // Aquí irá la lógica compleja para crear un elemento (RF13, RF15),
    // que necesitará recibir IDs de Tipo, Género y Usuario.
    // public Elemento createElemento(DtoElemento dto, Long userId) { ... }
}