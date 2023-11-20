package dev.funkos.dto;

import dev.rest.categoria.models.Categoria;
import dev.rest.funkos.dto.FunkoCreateDto;
import dev.rest.funkos.dto.FunkoUpdateDto;
import dev.rest.funkos.mappers.FunkoMapper;
import dev.rest.funkos.models.Funko;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FunkoMapperTest {
    private final Categoria categoria1 = Categoria.builder()
            .id(1L)
            .name("Categoria 1")
            .build();

    private final Funko funko = Funko.builder()
            .id(1L)
            .nombre("Funko 1")
            .precio(10.0)
            .cantidad(10)
            .rutaImagen("rutaImagen")
            .categoria(categoria1)
            .build();

    private final FunkoMapper funkoMapper = new FunkoMapper();

    @Test
    void toFunko() {
        FunkoCreateDto funkoCreateDto = new FunkoCreateDto(
                "Funko 1",
                10.0,
                10,
                "rutaImagen",
                categoria1.getName()
        );
        assertAll(
                () -> assertEquals(funko.getNombre(), funkoMapper.toFunko(funkoCreateDto, categoria1).getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoMapper.toFunko(funkoCreateDto, categoria1).getPrecio()),
                () -> assertEquals(funko.getCantidad(), funkoMapper.toFunko(funkoCreateDto, categoria1).getCantidad()),
                () -> assertEquals(funko.getRutaImagen(), funkoMapper.toFunko(funkoCreateDto, categoria1).getRutaImagen()),
                () -> assertEquals(funko.getCategoria(), funkoMapper.toFunko(funkoCreateDto, categoria1).getCategoria())
        );
    }

    @Test
    void testToFunko() {
        Long id = 1L;
        FunkoUpdateDto funkoUpdateDto = new FunkoUpdateDto(
                "Funko 1",
                10.0,
                10,
                "rutaImagen",
                categoria1.getName()
        );

        Funko funko = Funko.builder()
                .id(id)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(10)
                .rutaImagen("rutaImagen")
                .categoria(categoria1)
                .build();

        assertAll(
                () -> assertEquals(funko.getId(), funkoMapper.toFunko(funkoUpdateDto, funko, categoria1).getId()),
                () -> assertEquals(funko.getNombre(), funkoMapper.toFunko(funkoUpdateDto, funko, categoria1).getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoMapper.toFunko(funkoUpdateDto, funko, categoria1).getPrecio()),
                () -> assertEquals(funko.getCantidad(), funkoMapper.toFunko(funkoUpdateDto, funko, categoria1).getCantidad()),
                () -> assertEquals(funko.getRutaImagen(), funkoMapper.toFunko(funkoUpdateDto, funko, categoria1).getRutaImagen()),
                () -> assertEquals(funko.getCategoria(), funkoMapper.toFunko(funkoUpdateDto, funko, categoria1).getCategoria())

        );
    }

    @Test
    void toFunkoDto() {
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(10.0)
                .cantidad(10)
                .rutaImagen("rutaImagen")
                .categoria(categoria1)
                .build();

        assertAll(
                () -> assertEquals(funko.getNombre(), funkoMapper.toFunkoDto(funko).nombre()),
                () -> assertEquals(funko.getPrecio(), funkoMapper.toFunkoDto(funko).precio()),
                () -> assertEquals(funko.getCantidad(), funkoMapper.toFunkoDto(funko).cantidad()),
                () -> assertEquals(funko.getRutaImagen(), funkoMapper.toFunkoDto(funko).rutaImagen()),
                () -> assertEquals(funko.getCategoria(), funkoMapper.toFunkoDto(funko).categoria())
        );
    }
}