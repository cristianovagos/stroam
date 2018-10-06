package es1819.stroam.catalog.controller;

import es1819.stroam.catalog.errors.GenreNotFoundException;
import es1819.stroam.catalog.errors.ProductionNotFoundException;
import es1819.stroam.catalog.model.Genre;
import es1819.stroam.catalog.model.Production;
import es1819.stroam.catalog.repository.GenreRepository;
import es1819.stroam.catalog.repository.ProductionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.xml.ws.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class GenreController {

    @Autowired
    private GenreRepository genreRepository;

    @RequestMapping(value = "/catalog/genre", method = GET)
    public List<Genre> getAllGenres() { return genreRepository.findAll(); }

    @RequestMapping(value = "/catalog/genre", method = POST)
    public ResponseEntity<Object> addGenre(@RequestBody Genre genre) {
        Genre newGenre = genreRepository.save(genre);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(newGenre.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/catalog/genre/{id}", method = DELETE)
    public ResponseEntity<Object> removeCatalogByID(@PathVariable Long id) {
        genreRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/catalog/genre/{id}", method = GET)
    public Genre getGenreByID(@PathVariable("id") Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if(!genre.isPresent())
            throw new GenreNotFoundException("id=" + id);
        return genre.get();
    }
}
