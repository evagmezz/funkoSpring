package dev.rest.funkos.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record FunkoUpdateDto(
        @NotBlank(message = "El nombre no puede estar vacio")
        @Schema(description = "Nombre del funko", example = "Batman")
        String nombre,
        @Min(value = 0, message = "El precio debe ser mayor a 0")
        @Schema(description = "Precio del funko", example = "100.0")
        Double precio,
        @Min(value = 0, message = "La cantidad debe ser mayor a 0")
        @Schema(description = "Cantidad del funko", example = "10")
        Integer cantidad,
        @Schema(description = "Ruta de la imagen del funko", example = "imagen.jpg")
        String rutaImagen,
        @Schema(description = "Categoria del funko", example = "PELICULA")
        String categoria
) {

}
