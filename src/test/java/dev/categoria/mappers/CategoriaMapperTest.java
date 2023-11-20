package dev.categoria.mappers;

import dev.rest.categoria.dto.CategoriaDto;
import dev.rest.categoria.mappers.CategoriaMapper;
import dev.rest.categoria.models.Categoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaMapperTest {

    private final CategoriaMapper categoriaMapper = new CategoriaMapper();
    private final CategoriaDto categoriaDto = new CategoriaDto("Categoria 1", true);

    @Test
    void toCategoria() {
        Categoria categoria = categoriaMapper.toCategoria(categoriaDto);
        assertAll(
                () -> assertNotNull(categoria),
                () -> assertEquals(categoriaDto.nombre(), categoria.getName()),
                () -> assertNotNull(categoria.getCreatedAt()),
                () -> assertNotNull(categoria.getUpdatedAt()),
                () -> assertTrue(categoria.getIsDeleted())
        );
    }

    @Test
    void testToCategoria() {
        Long id = 1L;
        CategoriaDto categoriaDto = new CategoriaDto("DISNEY", false);

        Categoria categoria = Categoria.builder()
                .id(id)
                .name("DISNEY")
                .build();

        var resultado = categoriaMapper.toCategoria(categoriaDto, categoria);

        assertAll(
                () -> assertEquals(id, resultado.getId()),
                () -> assertEquals(categoriaDto.nombre(), resultado.getName()),
                () -> assertEquals(categoriaDto.isActive(), resultado.getIsDeleted()));
    }
}