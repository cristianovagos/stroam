package es1819.stroam.catalog.repository;

import es1819.stroam.catalog.model.Genre;
import es1819.stroam.catalog.model.Production;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findById(Long genreID);
    Optional<Genre> findByName(String genreName);
}
