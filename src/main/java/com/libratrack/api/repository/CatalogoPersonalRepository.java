package com.libratrack.api.repository;

import com.libratrack.api.entity.CatalogoPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * (Refactorizado por Seguridad)
 * Repositorio para la entidad CatalogoPersonal.
 * Se han añadido métodos que buscan usando 'username' (unidos a través
 * de la entidad Usuario) en lugar de solo 'usuarioId',
 * para dar soporte al nuevo CatalogoPersonalService (seguro).
 */
@Repository
public interface CatalogoPersonalRepository extends JpaRepository<CatalogoPersonal, Long> {

    /**
     * Busca todas las entradas del catálogo para un 'usuarioId' específico.
     * (Usado internamente por el servicio).
     */
    List<CatalogoPersonal> findByUsuarioId(Long usuarioId);
    
    /**
     * (NUEVO) Busca todas las entradas del catálogo por el 'username' del usuario.
     * Spring Data JPA entiende "Usuario_Username" y crea el 'JOIN' necesario.
     * Usado para RF08.
     */
    List<CatalogoPersonal> findByUsuario_Username(String username);

    /**
     * Busca una entrada específica combinando 'usuarioId' y 'elementoId'.
     * (Usado para la validación de duplicados en RF05).
     */
    Optional<CatalogoPersonal> findByUsuarioIdAndElementoId(Long usuarioId, Long elementoId);
    
    /**
     * (NUEVO) Busca una entrada específica por el 'username' y 'elementoId'.
     * Spring Data JPA entiende "Usuario_Username" y "Elemento_Id".
     * Usado para RF06 y RF07 (actualizar y borrar).
     */
    Optional<CatalogoPersonal> findByUsuario_UsernameAndElemento_Id(String username, Long elementoId);

}