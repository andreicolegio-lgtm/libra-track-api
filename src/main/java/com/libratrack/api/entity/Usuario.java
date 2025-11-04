package com.libratrack.api.entity;

import jakarta.persistence.*; // Importa todas las clases de JPA (persistencia)
import jakarta.validation.constraints.Email; // Para validar emails
import jakarta.validation.constraints.NotBlank; // Para campos no vacíos
import jakarta.validation.constraints.Size; // Para tamaños mínimos y máximos

@Entity // Indica a Spring Boot que esta clase es una tabla en la base de datos
@Table(name = "usuarios") // El nombre real de la tabla en MySQL será 'usuarios'
public class Usuario {

    @Id // Marca este campo como la clave primaria (ID) de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Le dice a MySQL que genere el ID automáticamente
    private Long id; // El tipo 'Long' es para IDs numéricos grandes

    @Column(unique = true, nullable = false) // Debe ser único (RF01) y no puede estar vacío
    @NotBlank(message = "El nombre de usuario no puede estar vacío") // Mensaje de error si está vacío
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres") // Longitud
    private String username;

    @Column(unique = true, nullable = false) // Debe ser único (RF01) y no puede estar vacío
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un formato de email válido") // Valida el formato del email
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    @Column(nullable = false) // No puede estar vacío (RF02)
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @Column(nullable = false)
    private Boolean esModerador = false; // Valor por defecto: un usuario normal no es moderador (RF14)

    // ===================================================================
    // CONSTRUCTORES
    // ===================================================================
    // Los constructores son métodos especiales para crear objetos Usuario.

    public Usuario() {
        // Constructor vacío (necesario para Spring Boot)
    }

    public Usuario(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        // esModerador se inicializa a false por defecto
    }

    // ===================================================================
    // GETTERS Y SETTERS
    // ===================================================================
    // Son métodos para acceder (get) y modificar (set) los valores de los campos.

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

    public Boolean getEsModerador() {
        return esModerador;
    }

    public void setEsModerador(Boolean esModerador) {
        this.esModerador = esModerador;
    }

    // ===================================================================
    // MÉTODO toString (Opcional, pero útil para depurar)
    // ===================================================================
    @Override // Indica que estamos "sobrescribiendo" un método de la clase padre
    public String toString() {
        return "Usuario{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", esModerador=" + esModerador +
               '}';
    }
}