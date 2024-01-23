package dev.rest.funkos.repositories;

import dev.rest.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunkoRepository extends JpaRepository<Funko, Long>, JpaSpecificationExecutor<Funko> {

    List<Funko> getByCategoriaNameContainingIgnoreCase(String categoria);

    List<Funko> getByNombreContainingIgnoreCase(String nombre);

    List<Funko> getByNombreAndCategoriaNameContainingIgnoreCase(String nombre, String categoria);

}
