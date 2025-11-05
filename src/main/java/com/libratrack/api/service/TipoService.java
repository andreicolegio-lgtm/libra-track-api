package com.libratrack.api.service;

import com.libratrack.api.entity.Tipo; // Importa la entidad Tipo
import com.libratrack.api.repository.TipoRepository; // Importa el repositorio Tipo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la lógica de negocio relacionada con la entidad Tipo.
 * Gestiona la creación y recuperación de Tipos de contenido (ej. "Serie", "Libro").
 */
@Service // Le dice a Spring que esta clase es un "Bean" de Servicio
public class TipoService {

    @Autowired // Inyecta la instancia del repositorio
    private TipoRepository tipoRepository;

    /**
     * Devuelve una lista de todos los Tipos disponibles en la base de datos.
     * Esto es útil para el frontend, para rellenar menús desplegables
     * (ej. en el formulario de "Proponer Elemento" o en filtros de búsqueda).
     *
     * @return Lista de todas las entidades Tipo.
     */
    public List<Tipo> getAllTipos() {
        return tipoRepository.findAll();
    }

    /**
     * Crea y guarda un nuevo Tipo en la base de datos.
     * Esta es una acción administrativa, llamada (por ahora) por el TipoController.
     * También es llamada internamente por el PropuestaElementoService si
     * un moderador aprueba un tipo que no existe.
     *
     * @param tipo El objeto Tipo a guardar (generalmente solo con el nombre).
     * @return La entidad Tipo tal como fue guardada (con su nuevo ID).
     * @throws Exception Si ocurre un error (ej. el nombre ya existe, violando
     * la restricción 'unique' de la BD).
     */
    public Tipo createTipo(Tipo tipo) throws Exception {
        // (En una V2, podríamos añadir validación aquí para comprobar si
        // tipo.getNombre() ya existe, antes de llamar a save())
        return tipoRepository.save(tipo);
    }
}