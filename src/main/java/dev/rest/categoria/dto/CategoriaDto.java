package dev.rest.categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CategoriaDto(
        @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
        @NotNull
        @Schema(description = "Nombre de la categoría", example = "Categoría 1")
        String nombre,
        @NotNull
        @Schema(description = "Estado de la categoría", example = "true")
        Boolean isActive

) {
}