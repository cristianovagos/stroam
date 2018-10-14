package es1819.stroam.catalog.repository;

import es1819.stroam.catalog.model.SeriesSeason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<SeriesSeason, Long> {
    Optional<SeriesSeason> findById(Long seasonID);
    List<SeriesSeason> findAllByProductionId(Long productionID);
    Optional<SeriesSeason> findByProductionIdAndSeason(Long productionID, int seasonNum);
    void deleteByProductionIdAndSeason(Long productionID, int seasonNum);
}
