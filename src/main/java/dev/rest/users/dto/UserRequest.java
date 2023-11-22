package dev.rest.users.dto;

import dev.rest.users.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Los apellidos no puede estar vacíos")
    private String apellidos;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;

    @Email(regexp = ".*@.*\\..*", message = "El email debe ser válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @NotBlank(message = "La password no puede estar vacía")
    @Length(min = 5, message = "La password debe tener al menos 5 caracteres")
    private String password;

    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);

    @Builder.Default
    private Boolean isDeleted = false;
}