package dev.rest.funkos.models;

import dev.rest.categoria.models.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Table(name = "funkos")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Funko {

    public static final String RUTA_IMAGEN = "/storage/";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del funko", example = "1")
    private Long id;

    @Column
    @NotBlank(message = "El nombre no puede estar vacio")
    @Schema(description = "Nombre del funko", example = "Batman")
    private String nombre;

    @Column
    @Min(value = 0, message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio del funko", example = "100.0")
    private Double precio = 0.0;

    @Column
    @Min(value = 0, message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad de funkos", example = "10")
    private Integer cantidad;

    @Column(name = "ruta_imagen")
    @NotBlank(message = "La ruta de la imagen no puede estar vacia")
    @Schema(description = "Ruta de la imagen del funko", example = "imagen.jpg")
    private String rutaImagen = RUTA_IMAGEN;

    @Column(name = "fecha_creacion")
    @Builder.Default
    @Schema(description = "Fecha de creación del funko", example = "2021-01-01")
    private LocalDate fechaCreacion = LocalDate.now();

    @Column(name = "fecha_actualizacion")
    @Builder.Default
    @Schema(description = "Fecha de actualización del funko", example = "2021-01-01")
    private LocalDate fechaActualizacion = LocalDate.now();

    @ManyToOne()
    @JoinColumn(name = "categoria_id")
    @NotNull
    @Schema(description = "Categoría del funko", example = "PELICULA")
    private Categoria categoria;

}
