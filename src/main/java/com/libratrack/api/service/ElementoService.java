package com.libratrack.api.service;

import com.libratrack.api.dto.ElementoDTO;
import com.libratrack.api.entity.Elemento;
import com.libratrack.api.entity.Genero;
import com.libratrack.api.entity.Tipo;
import com.libratrack.api.entity.Usuario;
import com.libratrack.api.model.EstadoContenido;
import com.libratrack.api.repository.ElementoRepository;
import com.libratrack.api.repository.GeneroRepository;
import com.libratrack.api.repository.TipoRepository;
import com.libratrack.api.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ElementoService {

    @Autowired
    private ElementoRepository elementoRepository;

    @Autowired
    private TipoRepository tipoRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    
    /**
     * Lógica de negocio para crear un nuevo Elemento a partir de un DTO (RF13).
     *
     * @param dto El DTO con los datos de la petición.
     * @return El Elemento guardado.
     * @throws Exception Si no se encuentra el Tipo, Genero o Usuario.
     */
    public Elemento createElemento(ElementoDTO dto) throws Exception {
        
        // 1. Buscar las entidades relacionadas por su ID
        Tipo tipo = tipoRepository.findById(dto.getTipoId())
                .orElseThrow(() -> new Exception("Tipo no encontrado con id: " + dto.getTipoId()));

        Usuario creador = usuarioRepository.findById(dto.getCreadorId())
                .orElseThrow(() -> new Exception("Usuario creador no encontrado con id: " + dto.getCreadorId()));
        
        Set<Genero> generos = new HashSet<>(generoRepository.findAllById(dto.getGeneroIds()));
        if (generos.isEmpty()) {
            throw new Exception("Se debe proporcionar al menos un género válido.");
        }

        // 2. Crear la nueva entidad Elemento
        Elemento nuevoElemento = new Elemento();
        nuevoElemento.setTitulo(dto.getTitulo());
        nuevoElemento.setDescripcion(dto.getDescripcion());
        nuevoElemento.setFechaLanzamiento(dto.getFechaLanzamiento());
        nuevoElemento.setImagenPortadaUrl(dto.getImagenPortadaUrl());
        
        // 3. Establecer las relaciones
        nuevoElemento.setTipo(tipo);
        nuevoElemento.setGeneros(generos);
        nuevoElemento.setCreador(creador); // Asignamos el proponente
        
        // 4. Establecer el estado (RF13 -> Propuesta comunitaria)
        nuevoElemento.setEstadoContenido(EstadoContenido.COMUNITARIO);

        // 5. Guardar en la base de datos
        return elementoRepository.save(nuevoElemento);
    }
}