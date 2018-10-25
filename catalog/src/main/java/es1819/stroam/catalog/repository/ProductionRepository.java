package es1819.stroam.catalog.repository;

import es1819.stroam.catalog.model.Production;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductionRepository extends JpaRepository<Production, Long> {
    Optional<Production> findById(Long productionID);
    Optional<List<Production>> findAllByName(String name);
    Optional<List<Production>> findAllByGenres_id(Long id);
}
