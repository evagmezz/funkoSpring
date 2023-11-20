package dev.funkos.repositories;

import dev.rest.categoria.models.Categoria;
import dev.rest.funkos.models.Funko;
import dev.rest.funkos.repositories.FunkoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class FunkoRepositoryTest {

    private final Categoria categoria = new Categoria(1L, "Categoria 1", LocalDateTime.now(), LocalDateTime.now(), true);

    private final Funko funko1 = Funko.builder()
            .id(1L)
            .nombre("Funko 1")
            .precio(100.0)
            .cantidad(10)
            .rutaImagen("ruta1")
            .categoria(categoria)
            .fechaCreacion(LocalDate.now())
            .fechaActualizacion(LocalDate.now())
            .build();

    private final Funko funko2 = Funko.builder()
            .id(2L)
            .nombre("Funko 2")
            .precio(200.0)
            .cantidad(20)
            .rutaImagen("ruta2")
            .categoria(categoria)
            .fechaCreacion(LocalDate.now())
            .fechaActualizacion(LocalDate.now())
            .build();

    @Autowired
    private FunkoRepository funkoRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        funkoRepository.deleteAll();
        entityManager.merge(categoria);
        entityManager.flush();

        entityManager.merge(funko1);
        entityManager.merge(funko2);
        entityManager.flush();
    }

    @Test
    void getByCategoriaNameContainingIgnoreCase() {
        funkoRepository.getByCategoriaNameContainingIgnoreCase(categoria.getName()).forEach(c -> {
            assertAll(
                    () -> assertEquals(categoria.getId(), c.getCategoria().getId()),
                    () -> assertEquals(categoria.getName(), c.getCategoria().getName()),
                    () -> assertEquals(categoria.getCreatedAt(), c.getCategoria().getCreatedAt()),
                    () -> assertEquals(categoria.getUpdatedAt(), c.getCategoria().getUpdatedAt()),
                    () -> assertEquals(categoria.getIsDeleted(), c.getCategoria().getIsDeleted())
            );
        });
    }

    @Test
    void getByNombreContainingIgnoreCase() {
        funkoRepository.getByNombreContainingIgnoreCase(funko1.getNombre()).forEach(f -> {
            assertAll(
                    () -> assertEquals(funko1.getId(), f.getId()),
                    () -> assertEquals(funko1.getNombre(), f.getNombre()),
                    () -> assertEquals(funko1.getPrecio(), f.getPrecio()),
                    () -> assertEquals(funko1.getCantidad(), f.getCantidad()),
                    () -> assertEquals(funko1.getRutaImagen(), f.getRutaImagen()),
                    () -> assertEquals(funko1.getCategoria().getId(), f.getCategoria().getId()),
                    () -> assertEquals(funko1.getCategoria().getName(), f.getCategoria().getName()),
                    () -> assertEquals(funko1.getCategoria().getCreatedAt(), f.getCategoria().getCreatedAt()),
                    () -> assertEquals(funko1.getCategoria().getUpdatedAt(), f.getCategoria().getUpdatedAt()),
                    () -> assertEquals(funko1.getCategoria().getIsDeleted(), f.getCategoria().getIsDeleted()),
                    () -> assertEquals(funko1.getFechaCreacion(), f.getFechaCreacion()),
                    () -> assertEquals(funko1.getFechaActualizacion(), f.getFechaActualizacion())
            );
        });
    }

    @Test
    void getByNombreAndCategoriaNameContainingIgnoreCase() {
        funkoRepository.getByNombreAndCategoriaNameContainingIgnoreCase(funko1.getNombre(), categoria.getName()).forEach(f -> {
            assertAll(
                    () -> assertEquals(funko1.getId(), f.getId()),
                    () -> assertEquals(funko1.getNombre(), f.getNombre()),
                    () -> assertEquals(funko1.getPrecio(), f.getPrecio()),
                    () -> assertEquals(funko1.getCantidad(), f.getCantidad()),
                    () -> assertEquals(funko1.getRutaImagen(), f.getRutaImagen()),
                    () -> assertEquals(funko1.getCategoria().getId(), f.getCategoria().getId()),
                    () -> assertEquals(funko1.getCategoria().getName(), f.getCategoria().getName()),
                    () -> assertEquals(funko1.getCategoria().getCreatedAt(), f.getCategoria().getCreatedAt()),
                    () -> assertEquals(funko1.getCategoria().getUpdatedAt(), f.getCategoria().getUpdatedAt()),
                    () -> assertEquals(funko1.getCategoria().getIsDeleted(), f.getCategoria().getIsDeleted()),
                    () -> assertEquals(funko1.getFechaCreacion(), f.getFechaCreacion()),
                    () -> assertEquals(funko1.getFechaActualizacion(), f.getFechaActualizacion())
            );
        });
    }
}