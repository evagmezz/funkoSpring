package dev.rest.categoria.repositories;

import dev.rest.categoria.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {

    Optional<List<Categoria>> findAllByNameContainingIgnoreCase(String nombre);

    Optional<Categoria> findByNameContainingIgnoreCase(String nombre);

    List<Categoria> isDeleted(boolean isActive);

    @Modifying
    @Query("UPDATE Categoria p SET p.isDeleted = false WHERE p.id = :id")
    void updateIsActiveToFalseById(Long id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Funko p WHERE p.categoria.id = :id")
    Boolean existsFunkoById(Long id);

    @Query("SELECT p.id FROM Categoria p WHERE p.name = :name")
    Optional<Long> getIdByName(String name);
}
