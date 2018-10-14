package es1819.stroam.catalog.repository;

import es1819.stroam.catalog.model.Episode;
import es1819.stroam.catalog.model.Production;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    Optional<Episode> findById(Long episodeID);
    Optional<Episode> findBySeasonAndEpisode(Long seasonID, int episodeNum);
    Optional<Episode> findByImdbID(String imdbID);
    List<Episode> findAllByTitle(String title);
    List<Episode> findAllBySeason(Long seasonID);
    void deleteBySeasonAndEpisode(Long seasonID, int episodeNum);
}
