package dev.rest.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequest {

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

    @NotBlank(message = "La password de comprobacion no puede estar vacía")
    @Length(min = 5, message = "La password de comprobacion debe tener al menos 5 caracteres")
    private String passwordComprobacion;

}