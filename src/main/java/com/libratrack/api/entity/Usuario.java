package com.libratrack.api.entity;

import jakarta.persistence.*; // Para anotaciones de la base de datos (Entidad, Tabla, ID...)
import jakarta.validation.constraints.Email; // Para la validación de formato de email
import jakarta.validation.constraints.NotBlank; // Para asegurar que un campo no esté vacío
import jakarta.validation.constraints.Size; // Para validar la longitud de los strings

/**
 * Entidad que representa la tabla 'usuarios' en la base de datos.
 * Almacena toda la información de autenticación y perfil del usuario.
 * Implementa los requisitos RF01, RF02, RF03, RF04.
 */
@Entity // Indica a Spring/JPA que esta clase es una entidad (una tabla)
@Table(name = "usuarios") // Especifica el nombre real de la tabla en MySQL
public class Usuario {

    @Id // Marca este campo como la Clave Primaria (PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Le dice a MySQL que genere el ID automáticamente (autoincremental)
    private Long id;

    /**
     * Nombre de usuario público. Usado para el login de seguridad (UserDetails).
     * RF01: Requerido y único.
     * RF04: Modificable por el usuario.
     */
    @Column(unique = true, nullable = false, length = 50) // Restricciones a nivel de BD
    @NotBlank(message = "El nombre de usuario no puede estar vacío") // Validación a nivel de API
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String username;

    /**
     * Email del usuario. Usado para el registro y la recuperación de cuenta.
     * RF01: Requerido y único. Usado para el login en la API.
     */
    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un formato de email válido") // Validación de formato
    private String email;

    /**
     * Contraseña *cifrada* (hasheada) del usuario.
     * RF02: Requerido.
     */
    @Column(nullable = false)
    @NotBlank(message = "La contraseña no puede estar vacía")
    // Nota: El @Size(min=8) se eliminó aquí porque la contraseña que guardamos
    // es el *hash* (largo), no la original. La validación de 8 caracteres
    // se hace en el DTO o en el servicio de registro.
    private String password; // Se almacena hasheada (ej. BCrypt)

    /**
     * Define el rol del usuario (RF03).
     * true = Moderador (puede aprobar propuestas, RF14).
     * false = Usuario (solo puede proponer, RF13).
     * (Los Administradores se pueden gestionar manualmente o con un rol separado).
     */
    @Column(nullable = false)
    private Boolean esModerador = false; // Valor por defecto 'false' (Usuario normal)

    
    // --- Constructores ---

    /**
     * Constructor vacío.
     * Requerido por JPA (el framework de base de datos) para crear instancias.
     */
    public Usuario() {
    }

    /**
     * Constructor de conveniencia para crear nuevos usuarios.
     */
    public Usuario(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        // esModerador se inicializa a 'false' por defecto según la definición del campo.
    }

    
    // --- Getters y Setters ---
    // Métodos públicos necesarios para que Spring (JPA y Jackson)
    // pueda leer y escribir en los campos privados de esta clase.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter para el campo booleano 'esModerador'.
     * Usado por UserDetailsServiceImpl para asignar roles (RF03).
     */
    public Boolean getEsModerador() {
        return esModerador;
    }

    public void setEsModerador(Boolean esModerador) {
        this.esModerador = esModerador;
    }
}