package dev.rest.funkos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record FunkoCreateDto(
        @NotBlank(message = "El nombre no puede estar vacio")
        String nombre,
        @Min(value = 0, message = "El precio debe ser mayor a 0")
        Double precio,
        @Min(value = 0, message = "La cantidad debe ser mayor a 0")
        Integer cantidad,
        @NotBlank(message = "La ruta de la imagen no puede estar vacia")
        String rutaImagen,
        @NotEmpty(message = "La categoria no puede estar vacia")
        String categoria
) {
}

