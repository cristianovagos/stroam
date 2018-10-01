package es1819.stroam.catalog.production;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductionRepository extends JpaRepository<Production, Long> {
    Optional<Production> findById(Long productionID);
}
