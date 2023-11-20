package dev.rest.funkos.mappers;


import dev.rest.categoria.models.Categoria;
import dev.rest.funkos.dto.FunkoCreateDto;
import dev.rest.funkos.dto.FunkoResponseDto;
import dev.rest.funkos.dto.FunkoUpdateDto;
import dev.rest.funkos.models.Funko;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

import java.time.LocalDate;


@Component
public class FunkoMapper {

    public Funko toFunko(FunkoCreateDto funkoCreateDto, Categoria categoria) {
        return Funko.builder()
                .id(null)
                .nombre(funkoCreateDto.nombre())
                .precio(funkoCreateDto.precio())
                .cantidad(funkoCreateDto.cantidad())
                .rutaImagen(funkoCreateDto.rutaImagen())
                .fechaCreacion(LocalDate.now())
                .fechaActualizacion(LocalDate.now())
                .categoria(categoria)
                .build();
    }

    public Funko toFunko(FunkoUpdateDto funkoUpdateDto, Funko funko, Categoria categoria) {
        return Funko.builder().id(funko.getId())
                .nombre(funkoUpdateDto.nombre() != null ? funkoUpdateDto.nombre() : funko.getNombre())
                .precio(funkoUpdateDto.precio() != null ? funkoUpdateDto.precio() : funko.getPrecio())
                .cantidad(funkoUpdateDto.cantidad() != null ? funkoUpdateDto.cantidad() : funko.getCantidad())
                .rutaImagen(funkoUpdateDto.rutaImagen() != null ? funkoUpdateDto.rutaImagen() : funko.getRutaImagen())
                .categoria(categoria)
                .build();
    }

    public FunkoResponseDto toFunkoDto(Funko funko) {
        return new FunkoResponseDto(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCantidad(),
                funko.getRutaImagen(),
                funko.getFechaCreacion(),
                funko.getFechaActualizacion(),
                funko.getCategoria()
        );
    }
}
