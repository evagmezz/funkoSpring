package dev.rest.categoria.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CategoriaDto(
        @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
        @NotNull
        String nombre,
        @NotNull
        Boolean isActive

) {
}