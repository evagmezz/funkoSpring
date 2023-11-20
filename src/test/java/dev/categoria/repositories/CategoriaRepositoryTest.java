package dev.categoria.repositories;

import dev.rest.categoria.models.Categoria;
import dev.rest.categoria.repositories.CategoriaRepository;
import dev.rest.funkos.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoriaRepositoryTest {

    private final Categoria categoria = new Categoria(1L, "Categoria 1", LocalDateTime.now(), LocalDateTime.now(), true);


    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.merge(categoria);
        entityManager.flush();
    }

    @Test
    void findByNameEqualsIgnoreCase() {
        List<Categoria> categoria = categoriaRepository.findAllByNameContainingIgnoreCase("Categoria 1").orElse(null);
        assertAll(
                () -> assertNotNull(categoria),
                () -> assertEquals("Categoria 1", categoria.get(0).getName())
        );
    }

    @Test
    void findByIsActive() {
        Categoria categoria = categoriaRepository.isDeleted(true).get(0);
        assertAll(
                () -> assertNotNull(categoria),
                () -> assertEquals("Categoria 1", categoria.getName())
        );

    }

    @Test
    void updateIsActiveToFalseById() {
        categoriaRepository.updateIsActiveToFalseById(1L);
        Categoria categoria = categoriaRepository.findById(1L).orElse(null);
        assertAll(
                () -> assertNotNull(categoria),
                () -> assertTrue(categoria.getIsDeleted())
        );
    }

    @Test
    void existsFunkoById() {
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(100.0)
                .cantidad(10)
                .rutaImagen("ruta")
                .fechaCreacion(LocalDate.now())
                .fechaActualizacion(LocalDate.now())
                .categoria(categoria)
                .build();

        entityManager.merge(funko);
        entityManager.flush();
        Boolean exists = categoriaRepository.existsFunkoById(1L);
        assertAll(
                () -> assertNotNull(exists),
                () -> assertTrue(exists)
        );
    }
}